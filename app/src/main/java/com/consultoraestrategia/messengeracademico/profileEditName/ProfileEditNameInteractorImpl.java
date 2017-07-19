package com.consultoraestrategia.messengeracademico.profileEditName;

import android.util.Log;

/**
 * Created by kike on 18/07/2017.
 */

public class ProfileEditNameInteractorImpl implements ProfileEditNameInteractor {

    private ProfileEditNameRepository repository;
    public static String TAG = ProfileEditNameInteractorImpl.class.getSimpleName();

    public ProfileEditNameInteractorImpl() {
        Log.d(TAG, "ProfileEditNameInteractorImpl");
        this.repository = new ProfileEditNameRepositoryImpl();
    }

    @Override
    public void executeEditProfileName(String mName, String phoneNumber,String uriParse) {
        repository.editProfileOptionsName(mName, phoneNumber,uriParse);
    }
}
