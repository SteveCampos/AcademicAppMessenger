
package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListaPadre {

    @SerializedName("personaId")
    @Expose
    private Integer personaId;
    @SerializedName("nombres")
    @Expose
    private String nombres;
    @SerializedName("apellidoPaterno")
    @Expose
    private String apellidoPaterno;
    @SerializedName("apellidoMaterno")
    @Expose
    private String apellidoMaterno;
    @SerializedName("celular")
    @Expose
    private String celular;
    @SerializedName("estadoId")
    @Expose
    private Integer estadoId;
    @SerializedName("usuario")
    @Expose
    private String usuario;
    @SerializedName("ValorHijo")
    @Expose
    private String valorHijo;
    @SerializedName("NombreTipo")
    @Expose
    private String nombreTipo;
    @SerializedName("listaPadre")
    @Expose
    private List<Object> listaPadre = null;
    @SerializedName("periodos")
    @Expose
    private List<Object> periodos = null;

    public Integer getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getValorHijo() {
        return valorHijo;
    }

    public void setValorHijo(String valorHijo) {
        this.valorHijo = valorHijo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public List<Object> getListaPadre() {
        return listaPadre;
    }

    public void setListaPadre(List<Object> listaPadre) {
        this.listaPadre = listaPadre;
    }

    public List<Object> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Object> periodos) {
        this.periodos = periodos;
    }

}
