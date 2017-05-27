/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Tonib
 */
@Entity
@Table(name = "categorias")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categorias.findAll", query = "SELECT c FROM Categorias c")
    , @NamedQuery(name = "Categorias.findByIdCategoria", query = "SELECT c FROM Categorias c WHERE c.idCategoria = :idCategoria")
    , @NamedQuery(name = "Categorias.findByNombre", query = "SELECT c FROM Categorias c WHERE c.nombre = :nombre")})
public class Categorias implements Serializable {

private static final long serialVersionUID = 1L;
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Basic(optional = false)
@Column(name = "idCategoria")
private Integer idCategoria;
@Basic(optional = false)
@NotNull
@Size(min = 1, max = 45)
@Column(name = "nombre")
private String nombre;
@OneToMany(cascade = CascadeType.ALL, mappedBy = "categoriaId")
private Collection<Noticias> noticiasCollection;

public Categorias() {
}

public Categorias(Integer idCategoria) {
    this.idCategoria = idCategoria;
}

public Categorias(Integer idCategoria, String nombre) {
    this.idCategoria = idCategoria;
    this.nombre = nombre;
}

public Integer getIdCategoria() {
    return idCategoria;
}

public void setIdCategoria(Integer idCategoria) {
    this.idCategoria = idCategoria;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}

@XmlTransient
public Collection<Noticias> getNoticiasCollection() {
    return noticiasCollection;
}

public void setNoticiasCollection(Collection<Noticias> noticiasCollection) {
    this.noticiasCollection = noticiasCollection;
}

@Override
public int hashCode() {
    int hash = 0;
    hash += (idCategoria != null ? idCategoria.hashCode() : 0);
    return hash;
}

@Override
public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Categorias)) {
        return false;
    }
    Categorias other = (Categorias) object;
    if ((this.idCategoria == null && other.idCategoria != null) || (this.idCategoria != null && !this.idCategoria.equals(other.idCategoria))) {
        return false;
    }
    return true;
}

@Override
public String toString() {
    return "entities.Categorias[ idCategoria=" + idCategoria + " ]";
}

}
