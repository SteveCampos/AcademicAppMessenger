package com.consultoraestrategia.messengeracademico.verification.events;

import com.consultoraestrategia.messengeracademico.entities.PhoneNumberVerified;

/**
 * Created by Steve on 17/02/2017.
 */
public class VerificationEvent {

    public static final int OnPhoneNumberSended = 0;
    public static final int OnPhoneNumberSendedError = 1;
    public static final int OnPhoneNumberVerificated = 2;
    public static final int OnPhoneNumberFailedToVerificated = 3;
    public static final int OnInvalidCode = 4;

    private int type;
    private PhoneNumberVerified phoneNumberVerified;
    private String error;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PhoneNumberVerified getPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(PhoneNumberVerified phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
