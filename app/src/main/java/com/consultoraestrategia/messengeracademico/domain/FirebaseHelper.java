package com.consultoraestrategia.messengeracademico.domain;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Steve on 17/02/2017.
 */

public class FirebaseHelper {
    private FirebaseDatabase database;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance();
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }


}
