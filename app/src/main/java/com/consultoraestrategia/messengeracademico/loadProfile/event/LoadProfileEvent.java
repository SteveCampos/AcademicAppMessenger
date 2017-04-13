package com.consultoraestrategia.messengeracademico.loadProfile.event;

import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 25/02/2017.
 */

public class LoadProfileEvent {

    public static final int OnProfileUploadSuccess = 0;
    public static final int OnProfileUploadError = 1;
    public static final int OnImageUploadToVerificated = 2;
    public static final int OnInvalidTypeFormat = 3;

    private int type;
    private String mPhotoUser;
    private String mName;
    private String mPhoneNumber;
    private Profile profile;
    private Photo photo;


    private String error;



    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getmPhotoUser() {
        return mPhotoUser;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmPhotoUser(String mPhotoUser) {
        this.mPhotoUser = mPhotoUser;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

