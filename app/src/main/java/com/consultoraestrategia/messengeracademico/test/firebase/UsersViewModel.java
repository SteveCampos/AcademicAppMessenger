package com.consultoraestrategia.messengeracademico.test.firebase;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.google.firebase.database.FirebaseDatabase;

public class UsersViewModel extends FireChildViewModel<User> {
    public UsersViewModel(UseCaseHandler handler) {
        super(
                handler,
                new FirebaseQueryLiveData(FirebaseDatabase.getInstance().getReference("/users")),
                User.class
        );
    }
}
