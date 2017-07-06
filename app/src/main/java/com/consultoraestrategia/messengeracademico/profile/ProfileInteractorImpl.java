package com.consultoraestrategia.messengeracademico.profile;

import android.content.ContentResolver;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfileInteractorImpl implements ProfileInteractor {
    //Comunica con el Repository

    private ProfileRepository repository;

    public ProfileInteractorImpl(ContentResolver resolver) {
        this.repository = new ProfileRepositoryImpl(resolver);
    }

    @Override
    public void executeProfileEdit(String phoneNumber) {
        repository.initProfileEdit(phoneNumber);
    }


    @Override
    public void executeProfileInformation(String phoneNumber) {
        repository.initProfileInformation(phoneNumber);
    }
}
