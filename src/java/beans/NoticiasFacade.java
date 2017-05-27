/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entities.Noticias;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Tonib
 */
@Stateless
public class NoticiasFacade extends AbstractFacade<Noticias> {

@PersistenceContext(unitName = "FIBWIPU")
private EntityManager em;

@Override
protected EntityManager getEntityManager() {
    return em;
}

public NoticiasFacade() {
    super(Noticias.class);
}


public void publicarNoticiaId(int id) {
    Noticias noticia = new Noticias();
    noticia = em.find(Noticias.class, id);
    noticia.setPublicada("S");
    em.flush();
}


public Noticias obtenerNoticiaPorId(int id){
    Noticias noticia = new Noticias();
    noticia = em.find(Noticias.class, id);
    if(noticia != null){
        return noticia;
    } else {
        return null;
    }  
}

public void sumarVisitas(int idNoticia){
    Query query = em.createNativeQuery("SELECT * FROM fibwi.noticias WHERE idNoticia=?",Noticias.class);
    query.setParameter(1,idNoticia);
    List<Noticias> listaNoticias = query.getResultList();
    Noticias noticia = (Noticias) listaNoticias.get(0);
    
    Query queryActualizar = em.createNativeQuery("UPDATE fibwi.noticias SET visitas=? WHERE idNoticia=?");
    queryActualizar.setParameter(1, noticia.getVisitas() +1);
    queryActualizar.setParameter(2, idNoticia);
    queryActualizar.executeUpdate();
    
}  

public List<Noticias> noticiasMasVistas() {
    List<Noticias> noticiasVistas;
    noticiasVistas = em.createNativeQuery("SELECT * FROM noticias ORDER BY visitas DESC", Noticias.class).getResultList();

    return noticiasVistas;
}

public void insertarNoticia(String firma, int IdCategoria, String titulo, String texto, String etiqueta, String fotos, int visitas, Date fecha, String publicada, String textopequeño) {

    Query query = em.createNativeQuery("INSERT INTO fibwi.noticias (autorId,categoriaId,titulo,texto,etiquetas,fotos,visitas, fecha, publicada,textopequeño) VALUES(?,?,?,?,?,?,?,?,?,?)");
    query.setParameter(1, firma);
    query.setParameter(2, IdCategoria);
    query.setParameter(3, titulo);
    query.setParameter(4, texto);
    query.setParameter(5, etiqueta);
    query.setParameter(6, fotos);
    query.setParameter(7, visitas);
    java.sql.Date fechad = new java.sql.Date(fecha.getTime());
    query.setParameter(8, fechad);
    query.setParameter(9, publicada);
    query.setParameter(10, textopequeño);
    query.executeUpdate();

}
}
