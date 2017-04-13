package com.consultoraestrategia.messengeracademico.main;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;

/**
 * Created by jairc on 27/03/2017.
 */

public interface MainPresenter {

    void onCreate();

    void onPause();

    void onStop();

    void onResume();

    void onDestroy();

    void getPhoneNumber(SharedPreferences preferences);

    void getContact(String phoneNumber);

    void onEventMainThread(MainEvent event);

    void listenForIncomingMessages(Contact contact);

    void launchChat(Contact contact);

}
