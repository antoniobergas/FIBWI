package clases;

import entities.Roles;
import clases.util.JsfUtil;
import clases.util.PaginationHelper;
import beans.RolesFacadeLocal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("rolesController")
@SessionScoped
public class RolesController implements Serializable {

    private Roles current;
    private DataModel items = null;
    @EJB
    private beans.RolesFacadeLocal ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Roles> listaRoles;

    public RolesController() {
        current = new Roles();
        listaRoles = new ArrayList<Roles>();
    }
    
    @PostConstruct
    public void init() {
        obtenerRoles();
    }
    
    public void resetearFormularioRoles() {
        current = new Roles();
    }

    private void obtenerRoles(){
        if(listaRoles != null){
            listaRoles.clear();
        }
        for(Object r:ejbFacade.findAll()){
            listaRoles.add((Roles)r);
        }
    }

    
        public void seleccionarRol() {

        if (current != null && current.getIdRol() > 0) {
            for(Roles r:listaRoles){
                if(r.getIdRol() == current.getIdRol()){
                    current.setNombre(r.getNombre()                                                                                                                                                                                                                                                                                );
                }
            }    
        }
 
    }
        
        
      public void modificarRoles(){
		FacesMessage mensaje = new FacesMessage();	
		
		if(current.getIdRol() > 0 ){
			

			String rolAnterior = current.getNombre();
			try {
				ejbFacade.edit(current);
				mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
				mensaje.setSummary("se ha modificado el rol \"" + rolAnterior +"\"");
			} catch (Exception e) {
				mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
				mensaje.setSummary("El nombre del rol que has modificado ya existe");
			}			
			
			
		} else {
			/* no hemos seleccionado ningun rol, asi que insertara uno nuevo */

			try {
				ejbFacade.create(current);
				mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
				mensaje.setSummary("has añadido un nuevo rol");
			} catch (Exception e) {
				mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
				mensaje.setSummary("el rol que intentas añadir ya existe");
			}
		}
		
		resetearFormularioRoles();
                obtenerRoles();
		FacesContext.getCurrentInstance().addMessage("menFormRoles", mensaje);        
    }     
      
      
	public void eliminarRol(){

		FacesMessage mensaje = new FacesMessage();
		
		if (current != null && current.getIdRol() > 0) {
			
			try {
				ejbFacade.remove(current);
				mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
				mensaje.setSummary("rol borrado correctamente");
			} catch (Exception e) {
				
			}
			
			
		} else {
			mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
			mensaje.setSummary("Debe seleccionar un rol del desplegable para eliminarlo");
		}
		
		FacesContext.getCurrentInstance().addMessage("formRoles:menFormRoles", mensaje);
		resetearFormularioRoles();
		obtenerRoles();		
	}      
      
    public Roles getSelected() {
        if (current == null) {
            current = new Roles();
            selectedItemIndex = -1;
        }
        return current;
    }

    
    private RolesFacadeLocal getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Roles) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Roles();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolesCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Roles) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolesUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Roles) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolesDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Roles getRoles(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    public Roles getCurrent() {
        return current;
    }

    public void setCurrent(Roles current) {
        this.current = current;
    }

    public List<Roles> getListaRoles() {
        return listaRoles;
    }

    public void setListaRoles(List<Roles> listaRoles) {
        this.listaRoles = listaRoles;
    }
    
    

    @FacesConverter(forClass = Roles.class)
    public static class RolesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RolesController controller = (RolesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rolesController");
            return controller.getRoles(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Roles) {
                Roles o = (Roles) object;
                return getStringKey(o.getIdRol());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Roles.class.getName());
            }
        }

    }

}
