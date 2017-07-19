package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfileRepository;

/**
 * Created by kike on 19/07/2017.
 */

public class ProfileEditImageInteractorImpl implements ProfileEditImageInteractor {
    ProfileEditImageRepository repository;

    public ProfileEditImageInteractorImpl() {
        this.repository = new ProfileEditImageRepositoryImpl();
    }

    @Override
    public void executeProfileEditImage(String mName, String phoneNumber, Uri uriParse) {
        repository.editProfileImage(mName, phoneNumber, uriParse);
    }


}
