package com.consultoraestrategia.messengeracademico.importGroups.entities.ui;

import android.util.ArrayMap;

import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.Map;

public class CrmeUser {
    private String id;
    private String phoneNumber;
    private String displayName;
    private boolean admin;
    private boolean tutor;
    private String name;
    private Contact contact;

    public CrmeUser() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isMessengerInstalled() {
        return contact != null;
    }


    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isTutor() {
        return tutor;
    }

    public void setTutor(boolean tutor) {
        this.tutor = tutor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Map<String, Object> toMap(String userId, String phonenumber, String displayName, boolean isAdmin, boolean tutor, String name) {
        Map<String, Object> map = new ArrayMap<>();
        map.put("/" + userId + "/phoneNumber", phonenumber);
        map.put("/" + userId + "/displayName", displayName);
        map.put("/" + userId + "/admin", isAdmin);
        map.put("/" + userId + "/tutor", tutor);
        map.put("/" + userId + "/name", name);
        return map;
    }

    public Map<String, Object> toMap() {
        return toMap(String.valueOf(id), phoneNumber, displayName, admin, tutor, name);
    }

    @Override
    public String toString() {
        return "CrmeUser{" +
                "id='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", displayName='" + displayName + '\'' +
                ", admin=" + admin +
                ", tutor=" + tutor +
                ", name='" + name + '\'' +
                ", contact=" + contact +
                '}';
    }
}
