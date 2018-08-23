
package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

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
    @SerializedName("listaPadre")
    @Expose
    private List<Object> listaPadre = null;
    @SerializedName("periodos")
    @Expose
    private List<Periodo> periodos = null;

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

    public List<Object> getListaPadre() {
        return listaPadre;
    }

    public void setListaPadre(List<Object> listaPadre) {
        this.listaPadre = listaPadre;
    }

    public List<Periodo> getPeriodos() {
        return periodos;
    }

    public void setPeriodos(List<Periodo> periodos) {
        this.periodos = periodos;
    }

}
