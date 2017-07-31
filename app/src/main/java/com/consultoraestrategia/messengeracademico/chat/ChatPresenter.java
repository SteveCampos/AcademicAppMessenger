package com.consultoraestrategia.messengeracademico.chat;

import android.content.Intent;

import com.consultoraestrategia.messengeracademico.BasePresenter;
import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatPresenter extends BasePresenter {

    void loadMessages(Chat chat);

    void loadEmisor();

    void loadReceptor(String phoneNumber);

    void manageIntent(Intent intent);

    void sendMessageText(String text);

    void readMessage(ChatMessage message);

    void changeWritingState(String text);

    void listenReceptorConnection();

    void listenReceptorAction();

    void onEventMainThread(ChatEvent event);


    void pickImage();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
