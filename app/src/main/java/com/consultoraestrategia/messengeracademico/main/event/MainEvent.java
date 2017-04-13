package com.consultoraestrategia.messengeracademico.main.event;

import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by jairc on 27/03/2017.
 */

public class MainEvent {

    public static final int TYPE_CONTACT = 100;
    public static final int TYPE_PHONENUMBER = 101;
    public static final int TYPE_LAUNCH_CHAT = 102;


    private int type;
    private Contact contact;
    private String phoneNumber;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
