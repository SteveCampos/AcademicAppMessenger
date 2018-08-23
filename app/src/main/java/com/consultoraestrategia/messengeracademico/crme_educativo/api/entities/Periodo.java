
package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Periodo {

    @SerializedName("periodoId")
    @Expose
    private Integer periodoId;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("estadoId")
    @Expose
    private Integer estadoId;
    @SerializedName("tipoId")
    @Expose
    private Integer tipoId;
    @SerializedName("superId")
    @Expose
    private Integer superId;
    @SerializedName("geoReferenciaId")
    @Expose
    private Integer geoReferenciaId;
    @SerializedName("organigramaId")
    @Expose
    private Integer organigramaId;
    @SerializedName("entidadId")
    @Expose
    private Integer entidadId;
    @SerializedName("activo")
    @Expose
    private Boolean activo;
    @SerializedName("cicloId")
    @Expose
    private Integer cicloId;
    @SerializedName("docenteId")
    @Expose
    private Integer docenteId;
    @SerializedName("gruponombre")
    @Expose
    private String gruponombre;
    @SerializedName("grupoId")
    @Expose
    private Integer grupoId;
    @SerializedName("nivelAcademico")
    @Expose
    private String nivelAcademico;
    @SerializedName("nivelAcademicoId")
    @Expose
    private Integer nivelAcademicoId;
    @SerializedName("tutorId")
    @Expose
    private Integer tutorId;
    @SerializedName("listaPadres")
    @Expose
    private List<ListaPadre> listaPadres = null;
    @SerializedName("usuarioCreacionId")
    @Expose
    private Integer usuarioCreacionId;
    @SerializedName("usuarioCreadorId")
    @Expose
    private Integer usuarioCreadorId;
    @SerializedName("fechaCreacion")
    @Expose
    private Integer fechaCreacion;
    @SerializedName("usuarioAccionId")
    @Expose
    private Integer usuarioAccionId;
    @SerializedName("fechaAccion")
    @Expose
    private Integer fechaAccion;
    @SerializedName("fechaEnvio")
    @Expose
    private Integer fechaEnvio;
    @SerializedName("fechaEntrega")
    @Expose
    private Integer fechaEntrega;
    @SerializedName("fechaRecibido")
    @Expose
    private Integer fechaRecibido;
    @SerializedName("fechaVisto")
    @Expose
    private Integer fechaVisto;
    @SerializedName("fechaRespuesta")
    @Expose
    private Integer fechaRespuesta;
    @SerializedName("getSTime")
    @Expose
    private String getSTime;

    public Integer getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Integer periodoId) {
        this.periodoId = periodoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Integer getTipoId() {
        return tipoId;
    }

    public void setTipoId(Integer tipoId) {
        this.tipoId = tipoId;
    }

    public Integer getSuperId() {
        return superId;
    }

    public void setSuperId(Integer superId) {
        this.superId = superId;
    }

    public Integer getGeoReferenciaId() {
        return geoReferenciaId;
    }

    public void setGeoReferenciaId(Integer geoReferenciaId) {
        this.geoReferenciaId = geoReferenciaId;
    }

    public Integer getOrganigramaId() {
        return organigramaId;
    }

    public void setOrganigramaId(Integer organigramaId) {
        this.organigramaId = organigramaId;
    }

    public Integer getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Integer entidadId) {
        this.entidadId = entidadId;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getCicloId() {
        return cicloId;
    }

    public void setCicloId(Integer cicloId) {
        this.cicloId = cicloId;
    }

    public Integer getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Integer docenteId) {
        this.docenteId = docenteId;
    }

    public String getGruponombre() {
        return gruponombre;
    }

    public void setGruponombre(String gruponombre) {
        this.gruponombre = gruponombre;
    }

    public Integer getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Integer grupoId) {
        this.grupoId = grupoId;
    }

    public String getNivelAcademico() {
        return nivelAcademico;
    }

    public void setNivelAcademico(String nivelAcademico) {
        this.nivelAcademico = nivelAcademico;
    }

    public Integer getNivelAcademicoId() {
        return nivelAcademicoId;
    }

    public void setNivelAcademicoId(Integer nivelAcademicoId) {
        this.nivelAcademicoId = nivelAcademicoId;
    }

    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public List<ListaPadre> getListaPadres() {
        return listaPadres;
    }

    public void setListaPadres(List<ListaPadre> listaPadres) {
        this.listaPadres = listaPadres;
    }

    public Integer getUsuarioCreacionId() {
        return usuarioCreacionId;
    }

    public void setUsuarioCreacionId(Integer usuarioCreacionId) {
        this.usuarioCreacionId = usuarioCreacionId;
    }

    public Integer getUsuarioCreadorId() {
        return usuarioCreadorId;
    }

    public void setUsuarioCreadorId(Integer usuarioCreadorId) {
        this.usuarioCreadorId = usuarioCreadorId;
    }

    public Integer getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Integer fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getUsuarioAccionId() {
        return usuarioAccionId;
    }

    public void setUsuarioAccionId(Integer usuarioAccionId) {
        this.usuarioAccionId = usuarioAccionId;
    }

    public Integer getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(Integer fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public Integer getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Integer fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Integer fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Integer getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(Integer fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }

    public Integer getFechaVisto() {
        return fechaVisto;
    }

    public void setFechaVisto(Integer fechaVisto) {
        this.fechaVisto = fechaVisto;
    }

    public Integer getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Integer fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getGetSTime() {
        return getSTime;
    }

    public void setGetSTime(String getSTime) {
        this.getSTime = getSTime;
    }

}
