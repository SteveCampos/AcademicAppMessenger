package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;

/**
 * Created by jairc on 12/04/2017.
 */

public class ConnectionInteractorImpl implements ConnectionInteractor {

    private ConnectionRepository repository;

    public ConnectionInteractorImpl(ConnectionRepository repository) {
        this.repository = repository;
    }

    @Override
    public void setOnline() {
        repository.setOnline();
    }

    @Override
    public void setOffline() {
        repository.setOffline();
    }
}
