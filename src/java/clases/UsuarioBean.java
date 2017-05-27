/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

import beans.RolesFacadeLocal;
import beans.UsuariosFacadeLocal;
import entities.Roles;
import entities.Usuarios;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class UsuarioBean implements Serializable {

    private Usuarios usuario;
    private List<Usuarios> listaUsuarios;
    private List<Roles> listaRoles;
    private Roles rolSeleccionado;
    private Usuarios usuarioSeleccionado;
    private int rolUsuarioSeleccionado;
    private Roles roles;
    private String passwordGuardada;
    @EJB
    private UsuariosFacadeLocal EJBUsuarios;
    @EJB
    private RolesFacadeLocal EJBRoles;

    public UsuarioBean() {

    }

    @PostConstruct
    public void init() {
        usuario = new Usuarios();
        roles = new Roles();
        listaUsuarios = new ArrayList<Usuarios>();
        listaRoles = new ArrayList<Roles>();
        rolSeleccionado = new Roles();
        obtenerUsuarios();
        obtenerRoles();
        resetearFormulario();
        resetearFormularioRoles();
    }

    private void obtenerUsuarios() {
        
        if (listaUsuarios != null) {
            listaUsuarios.clear();
        }
        for (Object o : EJBUsuarios.findAll()) {
            listaUsuarios.add((Usuarios) o);
        }

    }

    private void obtenerRoles() {
        if (listaRoles != null) {
            listaRoles.clear();
        }
        for (Object r : EJBRoles.findAll()) {
            listaRoles.add((Roles) r);
        }
    }

    public void resetearFormulario() {
        usuario = new Usuarios();
        usuarioSeleccionado = null;
        rolUsuarioSeleccionado = 0;
    }

    public void resetearFormularioRoles() {
        roles = new Roles();
        rolSeleccionado = new Roles();
    }

    public void seleccionarRegistro(SelectEvent event) {
        usuarioSeleccionado = (Usuarios) event.getObject();
        passwordGuardada = usuarioSeleccionado.getPassword();
        usuarioSeleccionado.setPassword(null);
        if (usuarioSeleccionado.getEmail() == null) {
            usuarioSeleccionado.setEmail("");
        } else if (usuarioSeleccionado.getFirma() == null) {
            usuarioSeleccionado.setFirma(usuarioSeleccionado.getIdUsuarios());
        }
    }

    public void editarUsuario() {

        if (usuarioSeleccionado != null) {

            rolUsuarioSeleccionado = usuarioSeleccionado.getRolId().getIdRol();
            roles.setIdRol(rolUsuarioSeleccionado);
            usuario.setRolId(roles);
            usuario.setIdUsuarios(usuarioSeleccionado.getIdUsuarios());
            usuario.setPassword(usuarioSeleccionado.getPassword());
            usuario.setEmail(usuarioSeleccionado.getEmail());
            usuario.setFirma(usuarioSeleccionado.getFirma());
        } else {

        }
    }

    public void eliminarUsuario() {
        FacesMessage mensaje = new FacesMessage();

        if (usuarioSeleccionado != null) {
            String usuarioBorrado = usuarioSeleccionado.getIdUsuarios();
            try {
                EJBUsuarios.remove(usuarioSeleccionado);
                mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
                mensaje.setSummary("usuario \"" + usuarioBorrado + "\" eliminado correctamente");
            } catch (Exception e) {
                mensaje.setSeverity(FacesMessage.SEVERITY_WARN);
                mensaje.setSummary("No fue posible eliminar al usuario " + usuarioBorrado);
            }

        } else {
            mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
            mensaje.setSummary("Debe seleccionar una fila para eliminar al usuario");
        }

        FacesContext.getCurrentInstance().addMessage("formUsuarios:menTablaUsuarios", mensaje);
        obtenerUsuarios();

    }

    /*
	 * Metodo para actualizar o guardar usuarios
     */
    public void modificarUsuario() {
        byte[] salt = new byte[16];
        FacesMessage mensaje = new FacesMessage();
        roles.setIdRol(rolUsuarioSeleccionado);
        usuario.setRolId(roles);
        /* Si existe usuarioSeleccionado modificamos el registro con los campos
		 *  si no, agregamos uno nuevo
         */
        if (usuarioSeleccionado != null) {

            if (usuario.getPassword() != null) {

                /*encriptamos contraseña*/
                usuario.setPassword(encriptarContrasenaSHA256(usuario.getPassword(), salt));

            } else {
                /* dejamos contraseña vacia*/
                usuario.setPassword(passwordGuardada);
            }

            try {
                EJBUsuarios.modificarUsuario(usuario);
                mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
                mensaje.setSummary("el usuario \"" + usuario.getIdUsuarios() + "\" ha sido modificado");

            } catch (Exception e) {
                mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
                mensaje.setSummary("No se pudo modificar ( ID ya existe )");
            }

        } else {
            /* inserción nuevos usuarios */
            usuario.setPassword(encriptarContrasenaSHA256(usuario.getPassword(), salt));
            try {
                EJBUsuarios.create(usuario);
                mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
                mensaje.setSummary("usuario dado de alta correctamente");

            } catch (Exception e) {
                mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
                mensaje.setSummary("ya existe un usuario con esa ID");
            }
        }
        FacesContext.getCurrentInstance().addMessage("menUsuario", mensaje);
        resetearFormulario();
        obtenerUsuarios();
        obtenerRoles();

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

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public List<Usuarios> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuarios> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public List<Roles> getListaRoles() {
        return listaRoles;
    }

    public void setListaRoles(List<Roles> listaRoles) {
        this.listaRoles = listaRoles;
    }

    public Roles getRolSeleccionado() {
        return rolSeleccionado;
    }

    public void setRolSeleccionado(Roles rolSeleccionado) {
        this.rolSeleccionado = rolSeleccionado;
    }

    public Usuarios getUsuarioSeleccionado() {
        return usuarioSeleccionado;
    }

    public void setUsuarioSeleccionado(Usuarios usuarioSeleccionado) {
        this.usuarioSeleccionado = usuarioSeleccionado;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public int getRolUsuarioSeleccionado() {
        return rolUsuarioSeleccionado;
    }

    public void setRolUsuarioSeleccionado(int rolUsuarioSeleccionado) {
        this.rolUsuarioSeleccionado = rolUsuarioSeleccionado;
    }

}
