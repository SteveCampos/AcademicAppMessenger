
package com.consultoraestrategia.messengeracademico.crme_educativo.api.entities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseCrmeGroups {

    @SerializedName("Successful")
    @Expose
    private Boolean successful;
    @SerializedName("Value")
    @Expose
    private List<Value> value = null;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

}
