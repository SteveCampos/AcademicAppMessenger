package com.consultoraestrategia.messengeracademico.crme_educativo.api;

import com.google.gson.annotations.SerializedName;

public class CrmeRequestBody<T> {
    @SerializedName("interface")
    private String _interface = "RestAPI";
    private String method;
    private T parameters;

    public CrmeRequestBody() {
    }


    public String get_interface() {
        return _interface;
    }

    public void set_interface(String _interface) {
        this._interface = _interface;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParameters() {
        return parameters;
    }

    public void setParameters(T parameters) {
        this.parameters = parameters;
    }
}
