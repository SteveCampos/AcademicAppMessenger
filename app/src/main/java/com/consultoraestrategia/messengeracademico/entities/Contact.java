package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 28/02/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
public class Contact extends BaseModel {


    @Column
    private String photoUri;

    @Column
    private String name;

    @Column
    private String phoneNumber;

    @Column
    @PrimaryKey
    private String userKey;


    public String getName() {
        return name;
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
        return super.equals(obj);
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userKey", userKey);
        return result;
    }
}
