package com.consultoraestrategia.messengeracademico.profile.event;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfileEvent {

    public static final int OnProfileEdit = 0;
    public static final int OnProfileEditError = 1;
    public static final int OnProfileInformation = 2;
    public static final int OnProfileInformationError = 3;

    private String phoneNumber;
    private long idPhone;
    private int type;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getIdPhone() {
        return idPhone;
    }

    public void setIdPhone(long idPhone) {
        this.idPhone = idPhone;
    }
}
