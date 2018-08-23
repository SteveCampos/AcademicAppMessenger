package com.consultoraestrategia.messengeracademico.chatList;

import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by jairc on 24/03/2017.
 */

public interface ChatListPresenter {

    void onCreateView();

    void onDestroy();

    void onPause();

    void onStop();

    void onResume();


    void getChats();

    void onEventMainThread(ChatListEvent event);
}
