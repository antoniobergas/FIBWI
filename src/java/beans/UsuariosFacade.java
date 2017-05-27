/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Usuarios;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author CASA
 */
@Stateless
public class UsuariosFacade extends AbstractFacade<Usuarios> implements UsuariosFacadeLocal {

    @PersistenceContext(unitName = "FIBWIPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuariosFacade() {
        super(Usuarios.class);
    }
    
    @Override
    public Usuarios iniciarSesion( Usuarios u ) {
        Usuarios usuario = null;
        String consulta = "SELECT * FROM fibwi.usuarios WHERE idUsuarios = ? and password=?";
        System.out.println(u.getIdUsuarios());
        System.out.println(u.getPassword());
        try {
            Query query = em.createNativeQuery(consulta,Usuarios.class);
            query.setParameter(1, u.getIdUsuarios());
            query.setParameter(2, u.getPassword());
            List<Usuarios> lista = query.getResultList();
            if (!lista.isEmpty()) {
                usuario = lista.get(0);
            }
        } catch (Exception e) {
            throw e;
        }

        return usuario;
    }
    
    @Override
    public void modificarUsuario( Usuarios u ){
        Usuarios usuario = new Usuarios();
        
        usuario = em.find(Usuarios.class, u.getIdUsuarios());
        usuario.setRolId(u.getRolId());
        usuario.setEmail(u.getEmail());
               
        if( u.getFirma() != null){
            usuario.setFirma(u.getFirma());
        } else {
            usuario.setFirma(u.getIdUsuarios());
        }
        usuario.setIdUsuarios(u.getIdUsuarios());
        usuario.setPassword(u.getPassword());
        em.flush();
    }
   
}
