package com.consultoraestrategia.messengeracademico.entities;

import java.util.HashMap;

/**
 * Created by kike on 11/04/2017.
 */

public class Profile {


    private String mStatus;
    private String mPhoneNumber;
    private String mName;
    private String userKey;
    private Photo photo;

    public Profile() {
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("photoUri", photo.getUrl());//Entidad ToMap
        result.put("status", mStatus);
        result.put("phoneNumber", mPhoneNumber);
        result.put("name", mName);
        result.put("userKey", userKey);
        return result;
    }
}
