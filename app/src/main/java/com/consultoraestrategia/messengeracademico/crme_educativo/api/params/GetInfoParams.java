package com.consultoraestrategia.messengeracademico.crme_educativo.api.params;

import com.google.gson.annotations.SerializedName;

public class GetInfoParams {
    @SerializedName("NroObservador")
    private String observador;
    @SerializedName("NroObservado")
    private String observado;

    public GetInfoParams() {
    }

    public GetInfoParams(String observador, String observado) {
        this.observador = observador;
        this.observado = observado;
    }

    public String getObservador() {
        return observador;
    }

    public void setObservador(String observador) {
        this.observador = observador;
    }

    public String getObservado() {
        return observado;
    }

    public void setObservado(String observado) {
        this.observado = observado;
    }
}
