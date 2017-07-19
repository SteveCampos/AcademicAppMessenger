package com.consultoraestrategia.messengeracademico.profileEditName.event;

import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 18/07/2017.
 */

public class ProfileEditEvent {

    public static final int OnProfileEditSuccess = 0;
    public static final int OnProfileEditdError = 1;

    private int type;
    private String mPhotoUser;
    private String mName;
    private String mPhoneNumber;
    private Profile profile;
    private String error;
    private Photo photo;

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
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

    public void setmPhotoUser(String mPhotoUser) {
        this.mPhotoUser = mPhotoUser;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
