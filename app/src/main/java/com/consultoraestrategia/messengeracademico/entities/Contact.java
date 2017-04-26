package com.consultoraestrategia.messengeracademico.entities;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 28/02/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
public class Contact extends BaseModel {

    private static final String TAG = "ContactEntity";
    @Column
    private String photoUri;

    @Column
    private String name;

    @Column
    private String phoneNumber;

    @Column
    @PrimaryKey
    private String userKey;

    public Contact() {
    }

    public Contact(String userKey) {
        this.userKey = userKey;
        this.load();
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

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }


    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof Contact) {
            Contact contactObj = (Contact) obj;
            Log.d(TAG, "chatKey: " + getUserKey());
            Log.d(TAG, "objChat id: " + contactObj.getUserKey());
            equal = this.getUserKey().equals(contactObj.getUserKey());
        }
        return equal;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userKey", userKey);
        result.put("phoneNumber", phoneNumber);
        return result;
    }

}
