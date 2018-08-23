package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseUpdatePersonaPhoneNumber {

    @SerializedName("Successful")
    @Expose
    private Boolean successful;
    @SerializedName("Value")
    @Expose
    private Boolean value;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

}