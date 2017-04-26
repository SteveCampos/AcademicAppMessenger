package com.consultoraestrategia.messengeracademico.main;

/**
 * Created by jairc on 12/04/2017.
 */

public class ConnectionInteractorImpl implements ConnectionInteractor {

    private ConnectionRepository repository;

    public ConnectionInteractorImpl() {
        this.repository = new ConnectionRepositoryImpl();
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
