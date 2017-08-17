package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.BasePresenter;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by jairc on 27/03/2017.
 */

public interface MainPresenter extends BasePresenter {

    void listenForMessages();

    void suscribeToNotifications(FirebaseUser mainUser);
}
