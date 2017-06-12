package com.consultoraestrategia.messengeracademico.entities;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
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
@Parcel(analyze={Contact.class})
public class Contact extends BaseModel {


    private static final String TAG = "ContactEntity";

    @Column
    @PrimaryKey
    public String userKey;

    @Column
    public String phoneNumber;

    @Column
    public String name;

    @Column
    public String photoUri;


    public Contact() {
    }

    public Contact(String userKey) {
        this.userKey = userKey;
        this.load();
    }

    public Contact(String userKey, String phoneNumber, String name, String photoUri) {
        this.userKey = userKey;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.photoUri = photoUri;
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
