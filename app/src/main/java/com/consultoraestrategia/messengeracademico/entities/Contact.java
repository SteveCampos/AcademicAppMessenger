package com.consultoraestrategia.messengeracademico.entities;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.google.firebase.database.Exclude;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 28/02/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
@Parcel(analyze = {Contact.class})
public class Contact extends BaseModel {


    private static final String TAG = "ContactEntity";

    @Column
    @PrimaryKey
    public String uid;

    @Column
    public String phoneNumber;

    @Column
    public String name;

    @Column
    public String displayName;


    @Column
    public String photoUrl;

    @Column
    public String email;

    @Column
    public int type;


    @Exclude
    public Connection lastConnection;


    public static final int TYPE_MAIN_CONTACT = 100;
    public static final int TYPE_NOT_ADDED = 1;
    public static final int TYPE_ADDED_AND_VISIBLE = 2;


    public Contact() {
    }

    public Contact(String uid) {
        this.uid = uid;
        this.load();
    }

    public Contact(String uid, String phoneNumber, String name, String photoUrl) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Contact(String uid, String phoneNumber, String name, String displayName, String photoUrl, String email, int type) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.email = email;
        this.type = type;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return phoneNumber;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Connection getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Connection lastConnection) {
        this.lastConnection = lastConnection;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof Contact) {
            Contact contactObj = (Contact) obj;
            Log.d(TAG, "uid: " + getUid());
            Log.d(TAG, "objChat id: " + contactObj.getUid());
            equal = this.getUid().equals(contactObj.getUid());
        }
        return equal;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("phoneNumber", phoneNumber);
        return result;
    }

    @Override
    public String toString() {
        return "uid: " + uid +
                ", phoneNumber: " + phoneNumber +
                ", name: " + name +
                ", displayName: " + displayName +
                ", photoUrl: " + photoUrl +
                ", email: " + email;
    }
}
