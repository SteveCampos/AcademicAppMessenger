package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseGetInfo {

    @SerializedName("Successful")
    @Expose
    private Boolean successful;
    @SerializedName("Value")
    @Expose
    private String value;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
