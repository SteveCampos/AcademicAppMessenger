package com.consultoraestrategia.messengeracademico.entities;

import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by @stevecampos on 10/08/2017.
 */

public class User {

    private String uid;
    private String displayName;
    private String phoneNumber;
    private String email;
    private Uri photoUri;
    private String idToken;

    public User() {
    }

    public User(String uid, String displayName, String phoneNumber, String email, Uri photoUri, String idToken) {
        this.uid = uid;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.photoUri = photoUri;
        this.idToken = idToken;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        if (phoneNumber != null) {
            result.put("phoneNumber", phoneNumber);
        }
        if (email != null) {
            result.put("email", email);
        }
        if (displayName != null && !TextUtils.isEmpty(displayName)) {
            result.put("displayName", displayName);
        }

        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            result.put("photoUrl", photoUri.toString());
        }
        return result;
    }
}
