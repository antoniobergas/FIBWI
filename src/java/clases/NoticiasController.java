package clases;

import entities.Noticias;
import clases.util.JsfUtil;
import clases.util.PaginationHelper;
import beans.NoticiasFacade;
import entities.Categorias;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;

import java.util.Date;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;

@Named("noticiasController")
@SessionScoped
public class NoticiasController implements Serializable {

private Noticias current;
private DataModel items = null;
@EJB
private beans.NoticiasFacade ejbFacadeNoticias;
@EJB
private beans.NoticiasFacade ejbNoticiasLocal;

private PaginationHelper pagination;
private int selectedItemIndex;
private List<Noticias> listaNoticias;
private List<Noticias> listaMasVistas;
private Noticias noticiaPublicada;
@ManagedProperty(value = "#{param.idNoticia}")
private int idLeerNoticia;
@ManagedProperty(value = "#{param.idNoticia}")
private int idNoticiaRevisar;
private List<Noticias> ultimasNoticias = new ArrayList<>();
private Categorias categoria;
List<Noticias> listaNoticiasPendientes = new ArrayList<>();
List<Noticias> listaNoticiasPublicadas = new ArrayList<>();

public NoticiasController() {
}

@PostConstruct
public void iniciarCategoria() {
    categoria = new Categorias();
    cargarListasNoticias();
}


public String busquedaNoticia() {
    return "busquedaNoticia";
}

public String recargar() {
    return "recargar";
}

/*metodo para subir distintas imagenes relacionadas con la noticia*/
public void subirImagenes(FileUploadEvent evento) throws IOException{
    try {
        String  nombreArchivo = "C:/temp/"+evento.getFile().getFileName();
        
        File resultado = new File(nombreArchivo);
        FileOutputStream salida = new FileOutputStream(resultado);
        
        byte[] buffer = new byte[8192];
        
        int bulk;
        
        InputStream entrada = evento.getFile().getInputstream();
        
        while(true){
            bulk = entrada.read(buffer);
            if(bulk<0){
                break;
            }
            salida.write(buffer,0,bulk);
            salida.flush();
        }
        
        salida.close();
        entrada.close();

    } catch (IOException e) {
    }
}

public void publicarNoticia(int id) {

    int idNoticia = id;
    try {
        ejbFacadeNoticias.publicarNoticiaId(idNoticia);
    } catch (Exception e) {
    }

    cargarListasNoticias();

}

public void eliminarNoticia(int id){
    current = ejbFacadeNoticias.obtenerNoticiaPorId(id);
    try {
     ejbFacadeNoticias.remove(current);
     FacesContext.getCurrentInstance().addMessage("msgNoticiasPublicadas", new FacesMessage("La noticia " + current.getIdNoticia() 
             + ": " + current.getTitulo() + " se ha eliminado correctamente"));
    } catch (Exception e) {
    }
    
    cargarListasNoticias();

}

public void noticiaById() {

    cargarListasNoticias();
    int id = idNoticiaRevisar;
        if (listaNoticias != null) {

            for (Noticias n : listaNoticias) {

                if (n.getIdNoticia() == id) {
                    current = new Noticias();
                    current.setIdNoticia(n.getIdNoticia());
                    current.setAutorId(n.getAutorId());
                    current.setCategoriaId(n.getCategoriaId());
                    current.setEtiquetas(n.getEtiquetas());
                    current.setFecha(n.getFecha());
                    current.setTexto(n.getTexto());
                    current.setTitulo(n.getTitulo());
                    current.setVisitas(n.getVisitas());
                }

            }

        } else {

        }

}
public List<Noticias> cargarPendientes() {
    if (listaNoticiasPendientes != null) {

        listaNoticiasPendientes.clear();
    }
    for (Noticias n : ejbNoticiasLocal.findAll()) {

        if (n.getPublicada().equals("N")) {

            listaNoticiasPendientes.add(n);
        }
    }
    return listaNoticiasPendientes;
}

public List<Noticias> cargarPublicadas() {
    if (listaNoticiasPublicadas != null) {
        listaNoticiasPublicadas.clear();
    }

    for (Noticias n : ejbNoticiasLocal.findAll()) {
        if (n.getPublicada().equals("S")) {
            listaNoticiasPublicadas.add(n);
        }
    }
    return listaNoticiasPublicadas;
}

public void cargarListasNoticias() {

    cargarPendientes();
    cargarPublicadas();
}

public Categorias getCategoria() {
    return categoria;
}

public void setCategoria(Categorias categoria) {
    this.categoria = categoria;
}

public void subirNoticia(String firma, int IdCategoria, String titulo, String texto, String etiqueta, String fotos, String textopequeño) {

    int visitas = 0;
    String publicada = "N";
    Date fecha = new Date();

    ejbFacadeNoticias.insertarNoticia(firma, IdCategoria, titulo, texto, etiqueta, fotos, visitas, fecha, publicada, textopequeño);

}

public void ultimasNoticias() {
    listaNoticias = ejbFacadeNoticias.findAll();
    for (Noticias noticia : listaNoticias) {
        if (noticia.getPublicada().equals("S")) {
            noticiaPublicada = noticia;
            ultimasNoticias.add(noticiaPublicada);

        }

    }
}

public String noticiasVistas(int indice, String atributo) {
    listaMasVistas = ejbFacadeNoticias.noticiasMasVistas();
    String dato = "";
    switch (atributo) {
    case "foto":
        dato = (String) listaMasVistas.get(indice).getFotos();
        break;
    case "titulo":
        dato = (String) listaMasVistas.get(indice).getTitulo();
        break;
    }

    return dato;

}

public String datosNoticias(int indice, String atributo) {

    String dato = "";
    ultimasNoticias();
    switch (atributo) {
    case "foto":
        dato = ultimasNoticias.get(ultimasNoticias.size() - indice).getFotos();

        break;

    case "titulo":
        dato = ultimasNoticias.get(ultimasNoticias.size() - indice).getTitulo();

        break;
    case "texto":
        dato = ultimasNoticias.get(ultimasNoticias.size() - indice).getTextopequeño();

        break;
    case "firma":
        dato = ultimasNoticias.get(ultimasNoticias.size() - indice).getAutorId().getFirma();
        System.out.println(dato);
        break;
    }

    return dato;

}

public Date fechaNoticia(int indice) {
    ultimasNoticias();
    Date fecha;
    fecha = ultimasNoticias.get(ultimasNoticias.size() - indice).getFecha();

    return fecha;
}

public String noticiaPorId(int indice) {
    current = ejbFacadeNoticias.find(indice);
    System.out.println(current);
    return null;
}

public int idNoticia(int indice) {
    ultimasNoticias();
    int idNoticia;
    idNoticia = ultimasNoticias.get(ultimasNoticias.size() - indice).getIdNoticia();
    return idNoticia;
}

public Noticias getSelected() {
    if (current == null) {
        current = new Noticias();
        selectedItemIndex = -1;
    }
    return current;
}

private NoticiasFacade getFacade() {
    return ejbFacadeNoticias;
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
    current = (Noticias) getItems().getRowData();
    selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
    return "View";
}

public String prepareCreate() {
    current = new Noticias();
    selectedItemIndex = -1;
    return "Create";
}

public String create() {
    try {
        getFacade().create(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("NoticiasCreated"));
        return prepareCreate();
    } catch (Exception e) {
        JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        return null;
    }
}

public String prepareEdit() {
    current = (Noticias) getItems().getRowData();
    selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
    return "Edit";
}

public String update() {
    try {
        getFacade().edit(current);
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("NoticiasUpdated"));
        return "View";
    } catch (Exception e) {
        JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        return null;
    }
}

public String destroy() {
    current = (Noticias) getItems().getRowData();
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
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("NoticiasDeleted"));
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
    return JsfUtil.getSelectItems(ejbFacadeNoticias.findAll(), false);
}

public SelectItem[] getItemsAvailableSelectOne() {
    return JsfUtil.getSelectItems(ejbFacadeNoticias.findAll(), true);
}

public Noticias getNoticias(java.lang.Integer id) {
    return ejbFacadeNoticias.find(id);
}

public int getIdLeerNoticia() {
    return idLeerNoticia;
}

public void setIdLeerNoticia(int idLeerNoticia) {
    this.idLeerNoticia = idLeerNoticia;
}

public Noticias getCurrent() {
    return current;
}

public void setCurrent(Noticias current) {
    this.current = current;
}

@FacesConverter(forClass = Noticias.class)
public static class NoticiasControllerConverter implements Converter {

@Override
public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
    if (value == null || value.length() == 0) {
        return null;
    }
    NoticiasController controller = (NoticiasController) facesContext.getApplication().getELResolver().
            getValue(facesContext.getELContext(), null, "noticiasController");
    return controller.getNoticias(getKey(value));
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
    if (object instanceof Noticias) {
        Noticias o = (Noticias) object;
        return getStringKey(o.getIdNoticia());
    } else {
        throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Noticias.class.getName());
    }
}

}

public NoticiasFacade getEjbFacadeNoticias() {
    return ejbFacadeNoticias;
}

public void setEjbFacadeNoticias(NoticiasFacade ejbFacadeNoticias) {
    this.ejbFacadeNoticias = ejbFacadeNoticias;
}

public int getSelectedItemIndex() {
    return selectedItemIndex;
}

public void setSelectedItemIndex(int selectedItemIndex) {
    this.selectedItemIndex = selectedItemIndex;
}

public List<Noticias> getListaNoticias() {
    return listaNoticias;
}

public void setListaNoticias(List<Noticias> listaNoticias) {
    this.listaNoticias = listaNoticias;
}

public List<Noticias> getListaMasVistas() {
    return listaMasVistas;
}

public void setListaMasVistas(List<Noticias> listaMasVistas) {
    this.listaMasVistas = listaMasVistas;
}

public Noticias getNoticiaPublicada() {
    return noticiaPublicada;
}

public void setNoticiaPublicada(Noticias noticiaPublicada) {
    this.noticiaPublicada = noticiaPublicada;
}

public int getIdNoticiaRevisar() {
    return idNoticiaRevisar;
}

public void setIdNoticiaRevisar(int idNoticiaRevisar) {
    this.idNoticiaRevisar = idNoticiaRevisar;
}

public List<Noticias> getUltimasNoticias() {
    return ultimasNoticias;
}

public void setUltimasNoticias(List<Noticias> ultimasNoticias) {
    this.ultimasNoticias = ultimasNoticias;
}

public List<Noticias> getListaNoticiasPendientes() {
    return listaNoticiasPendientes;
}

public void setListaNoticiasPendientes(List<Noticias> listaNoticiasPendientes) {
    this.listaNoticiasPendientes = listaNoticiasPendientes;
}

public List<Noticias> getListaNoticiasPublicadas() {
    return listaNoticiasPublicadas;
}

public void setListaNoticiasPublicadas(List<Noticias> listaNoticiasPublicadas) {
    this.listaNoticiasPublicadas = listaNoticiasPublicadas;
}

}
