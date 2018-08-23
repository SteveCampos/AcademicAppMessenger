package com.consultoraestrategia.messengeracademico.crme_educativo.api;

import com.google.gson.annotations.SerializedName;

public class PhonenumberParams {
    @SerializedName("NroPhone")
    private String phonenumber;

    public PhonenumberParams() {
    }

    public PhonenumberParams(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
