package clases;

import entities.Usuarios;
import clases.util.JsfUtil;
import clases.util.PaginationHelper;
import beans.UsuariosFacadeLocal;
import entities.Roles;
import java.io.IOException;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

@Named("usuariosController")
@SessionScoped
public class UsuariosController implements Serializable {

private Usuarios current;
private DataModel items = null;
@EJB
private beans.UsuariosFacadeLocal ejbUsuarios;
private PaginationHelper pagination;
private int selectedItemIndex;
private Roles rol;
private String nuevaPassword;

public UsuariosController() {
    
}

@PostConstruct
public void init() {
    current = new Usuarios();
    rol = new Roles();

}
public String enviarALogin(){
    current.setPassword("");
    return "enviarALogin";
}

public String irFront(){
    return "irFront";
}

public String panelRegistro() {
    return "panelRegistro";
}

/*
  
       ZONA LOGIN Y PERMISOS
   
 */
public String cambiarContrasena() {
    current.setPassword("");
    return "cambiarContrasena";
}

public void cambiarPass() {
    byte[] salt = new byte[16];
    Usuarios usuario = new Usuarios();
    usuario = (Usuarios) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
    current.setPassword(encriptarContrasenaSHA256(current.getPassword(), salt));
    if (usuario.getPassword().equals(current.getPassword())) {
        current.setIdUsuarios(usuario.getIdUsuarios());
        current.setPassword(encriptarContrasenaSHA256(nuevaPassword, salt));
        current.setEmail(usuario.getEmail());
        current.setRolId(usuario.getRolId());
        current.setFirma(usuario.getFirma());
        ejbUsuarios.edit(current);
    }
}

public String iniciarSesion() {
    FacesMessage mensaje = new FacesMessage();
    String redireccion = null;
    Usuarios usuario;
    byte[] salt = new byte[16];
    current.setPassword(encriptarContrasenaSHA256(current.getPassword(), salt));
    try {
        usuario = ejbUsuarios.iniciarSesion(current);
        
        if (usuario != null) {
            if (usuario.getRolId().getIdRol() == 1) {
                redireccion = "logueadoComoAdmin";
            } else if (usuario.getRolId().getIdRol() == 7) {
                redireccion = "logueadoComoAyto";
            } else {
                redireccion = "logueadoComoOtro";
            }
            /* almacenamos usuario en sesion */
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
            current.setPassword("");
        } else {
            mensaje.setSeverity(FacesMessage.SEVERITY_WARN);
            mensaje.setSummary("Credenciales incorrectas");
            current.setIdUsuarios("");
            current.setPassword("");
        }
        
    } catch (Exception e) {
      
    }
    
    if (mensaje.getSummary() != null) {
        FacesContext.getCurrentInstance().addMessage("msgLogin", mensaje);
    }
    return redireccion;
}


  public void esAdmin(){
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Usuarios us = (Usuarios) context.getExternalContext().getSessionMap().get("usuario");
            if (us != null) {
                if ( !(us.getRolId().getNombre().equals("Administrador")) && us.getRolId().getNombre().equals("Ayuntamiento")) {
                    context.getExternalContext().redirect("./../AYTO/ayto.xhtml");
                }
            } else {
                context.getExternalContext().redirect("./../LOGIN/login.xhtml");
            }

        } catch (IOException e) {
        }
	
    }
    
    
        public void esAyto(){
    	try {
        	FacesContext context = FacesContext.getCurrentInstance();
    		Usuarios us = (Usuarios) context.getExternalContext().getSessionMap().get("usuario");
    		if(us != null) {
	        	if( !(us.getRolId().getNombre().equals("Administrador")) && !(us.getRolId().getNombre().equals("Ayuntamiento"))){
	        		context.getExternalContext().redirect("./../LOGIN/login.xhtml");
	        	} 
    		} else {
    			context.getExternalContext().redirect("./../LOGIN/login.xhtml");
    		}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

/*
   
    ALTA USUARIOS
   
 */
public void altaUsuario() {
    FacesMessage mensaje = new FacesMessage();
    byte[] salt = new byte[16];
    if (current != null) {
        current.setFirma(current.getIdUsuarios());
        rol.setIdRol(7);
        current.setRolId(rol);
        current.setPassword(encriptarContrasenaSHA256(current.getPassword(), salt));
        
        try {
            ejbUsuarios.create(current);
            mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
            mensaje.setSummary("usuario dado de alta correctamente");
        } catch (Exception e) {
            mensaje.setSeverity(FacesMessage.SEVERITY_WARN);
            mensaje.setSummary("Existen usuarios con el mismo nombre o email");
        }
    }
    
    FacesContext.getCurrentInstance().addMessage("registerForm:mensajes", mensaje);
    
}

public String moverRegreso() {
    return "moverRegreso";
}

public String cerrarSesion() {
    FacesContext context = FacesContext.getCurrentInstance();
    context.getExternalContext().invalidateSession();
    return "cerrarSesion";
    
}

private static String encriptarContrasenaSHA256(String passwordToHash, byte[] salt) {
    String generatedPassword = null;
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] bytes = md.digest(passwordToHash.getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    }
    return generatedPassword;
}

public Usuarios getSelected() {
    if (current == null) {
        current = new Usuarios();
        selectedItemIndex = -1;
        System.out.println(current);
    }
    return current;
}

private UsuariosFacadeLocal getFacade() {
    return ejbUsuarios;
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
    current = (Usuarios) getItems().getRowData();
    selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
    return "View";
}

public String prepareCreate() {
    current = new Usuarios();
    selectedItemIndex = -1;
    return "Create";
}

public String create() {
    try {
        getFacade().create(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuariosCreated"));
        return prepareCreate();
    } catch (Exception e) {
        JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        return null;
    }
}

public String prepareEdit() {
    current = (Usuarios) getItems().getRowData();
    selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
    return "Edit";
}

public String update() {
    try {
        getFacade().edit(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuariosUpdated"));
        return "View";
    } catch (Exception e) {
        JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        return null;
    }
}

public String destroy() {
    current = (Usuarios) getItems().getRowData();
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
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuariosDeleted"));
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
    return JsfUtil.getSelectItems(ejbUsuarios.findAll(), false);
}

public SelectItem[] getItemsAvailableSelectOne() {
    return JsfUtil.getSelectItems(ejbUsuarios.findAll(), true);
}

public Usuarios getUsuarios(java.lang.String id) {
    return ejbUsuarios.find(id);
}

public Usuarios getCurrent() {
    return current;
}

public void setCurrent(Usuarios current) {
    this.current = current;
}

public UsuariosFacadeLocal getEjbFacade() {
    return ejbUsuarios;
}

public void setEjbFacade(UsuariosFacadeLocal ejbFacade) {
    this.ejbUsuarios = ejbFacade;
}

public int getSelectedItemIndex() {
    return selectedItemIndex;
}

public void setSelectedItemIndex(int selectedItemIndex) {
    this.selectedItemIndex = selectedItemIndex;
}

@FacesConverter(forClass = Usuarios.class)
public static class UsuariosControllerConverter implements Converter {

@Override
public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
    if (value == null || value.length() == 0) {
        return null;
    }
    UsuariosController controller = (UsuariosController) facesContext.getApplication().getELResolver().
            getValue(facesContext.getELContext(), null, "usuariosController");
    return controller.getUsuarios(getKey(value));
}

java.lang.String getKey(String value) {
    java.lang.String key;
    key = value;
    return key;
}

String getStringKey(java.lang.String value) {
    StringBuilder sb = new StringBuilder();
    sb.append(value);
    return sb.toString();
}

@Override
public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
    if (object == null) {
        return null;
    }
    if (object instanceof Usuarios) {
        Usuarios o = (Usuarios) object;
        return getStringKey(o.getIdUsuarios());
    } else {
        throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuarios.class.getName());
    }
}

}

public String getNuevaPassword() {
    return nuevaPassword;
}

public void setNuevaPassword(String nuevaPassword) {
    this.nuevaPassword = nuevaPassword;
}

}
