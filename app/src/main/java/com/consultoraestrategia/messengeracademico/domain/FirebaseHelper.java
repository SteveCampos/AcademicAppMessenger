package com.consultoraestrategia.messengeracademico.domain;


import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by @stevecampos on 17/02/2017.
 */

public class FirebaseHelper {
    private FirebaseDatabase database;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public interface Listener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface CompletionListener<T>{
        void onSuccess(T data);
        void onFailure(Exception ex);
    }

}
