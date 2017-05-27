/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Tonib
 */
@Entity
@Table(name = "noticias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Noticias.findAll", query = "SELECT n FROM Noticias n")
    , @NamedQuery(name = "Noticias.findByIdNoticia", query = "SELECT n FROM Noticias n WHERE n.idNoticia = :idNoticia")
    , @NamedQuery(name = "Noticias.findByTitulo", query = "SELECT n FROM Noticias n WHERE n.titulo = :titulo")
    , @NamedQuery(name = "Noticias.findByTexto", query = "SELECT n FROM Noticias n WHERE n.texto = :texto")
    , @NamedQuery(name = "Noticias.findByEtiquetas", query = "SELECT n FROM Noticias n WHERE n.etiquetas = :etiquetas")
    , @NamedQuery(name = "Noticias.findByFotos", query = "SELECT n FROM Noticias n WHERE n.fotos = :fotos")
    , @NamedQuery(name = "Noticias.findByVisitas", query = "SELECT n FROM Noticias n WHERE n.visitas = :visitas")
    , @NamedQuery(name = "Noticias.findByFecha", query = "SELECT n FROM Noticias n WHERE n.fecha = :fecha")
    , @NamedQuery(name = "Noticias.findByPublicada", query = "SELECT n FROM Noticias n WHERE n.publicada = :publicada")})
public class Noticias implements Serializable {

@Basic(optional = false)
@NotNull
@Size(min = 1, max = 100)
@Column(name = "textopeque\u00f1o")
private String textopequeño;

private static final long serialVersionUID = 1L;
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Basic(optional = false)
@Column(name = "idNoticia")
private Integer idNoticia;
@Basic(optional = false)
@NotNull
@Size(min = 1, max = 45)
@Column(name = "titulo")
private String titulo;
@Basic(optional = false)
@NotNull
@Size(min = 1, max = 500)
@Column(name = "texto")
private String texto;
@Size(max = 45)
@Column(name = "etiquetas")
private String etiquetas;
@Size(max = 45)
@Column(name = "fotos")
private String fotos;
@Basic(optional = false)
@NotNull
@Column(name = "visitas")
private int visitas;
@Basic(optional = false)
@NotNull
@Column(name = "fecha")
@Temporal(TemporalType.DATE)
private Date fecha;
@Size(max = 5)
@Column(name = "publicada")
private String publicada;
@JoinColumn(name = "categoriaId", referencedColumnName = "idCategoria")
@ManyToOne(optional = false)
private Categorias categoriaId;
@JoinColumn(name = "autorId", referencedColumnName = "idUsuarios")
@ManyToOne(optional = false)
private Usuarios autorId;
@OneToMany(cascade = CascadeType.ALL, mappedBy = "noriciaId")
private Collection<Comentarios> comentariosCollection;

public Noticias() {
}

public Noticias(Integer idNoticia) {
    this.idNoticia = idNoticia;
}

public Noticias(Integer idNoticia, String titulo, String texto, int visitas, Date fecha) {
    this.idNoticia = idNoticia;
    this.titulo = titulo;
    this.texto = texto;
    this.visitas = visitas;
    this.fecha = fecha;
}

public Integer getIdNoticia() {
    return idNoticia;
}

public void setIdNoticia(Integer idNoticia) {
    this.idNoticia = idNoticia;
}

public String getTitulo() {
    return titulo;
}

public void setTitulo(String titulo) {
    this.titulo = titulo;
}

public String getTexto() {
    return texto;
}

public void setTexto(String texto) {
    this.texto = texto;
}

public String getEtiquetas() {
    return etiquetas;
}

public void setEtiquetas(String etiquetas) {
    this.etiquetas = etiquetas;
}

public String getFotos() {
    return fotos;
}

public void setFotos(String fotos) {
    this.fotos = fotos;
}

public int getVisitas() {
    return visitas;
}

public void setVisitas(int visitas) {
    this.visitas = visitas;
}

public Date getFecha() {
    return fecha;
}

public void setFecha(Date fecha) {
    this.fecha = fecha;
}

public String getPublicada() {
    return publicada;
}

public void setPublicada(String publicada) {
    this.publicada = publicada;
}

public Categorias getCategoriaId() {
    return categoriaId;
}

public void setCategoriaId(Categorias categoriaId) {
    this.categoriaId = categoriaId;
}

public Usuarios getAutorId() {
    return autorId;
}

public void setAutorId(Usuarios autorId) {
    this.autorId = autorId;
}

@XmlTransient
public Collection<Comentarios> getComentariosCollection() {
    return comentariosCollection;
}

public void setComentariosCollection(Collection<Comentarios> comentariosCollection) {
    this.comentariosCollection = comentariosCollection;
}

@Override
public int hashCode() {
    int hash = 0;
    hash += (idNoticia != null ? idNoticia.hashCode() : 0);
    return hash;
}

@Override
public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Noticias)) {
        return false;
    }
    Noticias other = (Noticias) object;
    if ((this.idNoticia == null && other.idNoticia != null) || (this.idNoticia != null && !this.idNoticia.equals(other.idNoticia))) {
        return false;
    }
    return true;
}

@Override
public String toString() {
    return "entities.Noticias[ idNoticia=" + idNoticia + " ]";
}

public String getTextopequeño() {
    return textopequeño;
}

public void setTextopequeño(String textopequeño) {
    this.textopequeño = textopequeño;
}

}
