package com.consultoraestrategia.messengeracademico.crme_educativo.api.params;

import com.google.gson.annotations.SerializedName;

public class UpdatePersonaPhoneNumberParams {
    @SerializedName("idpersona")
    private String personaId;
    @SerializedName("nroPhone")
    private String phoneNumber;

    public UpdatePersonaPhoneNumberParams() {
    }

    public UpdatePersonaPhoneNumberParams(String personaId, String phoneNumber) {
        this.personaId = personaId;
        this.phoneNumber = phoneNumber;
    }

    public String getPersonaId() {
        return personaId;
    }

    public void setPersonaId(String personaId) {
        this.personaId = personaId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
