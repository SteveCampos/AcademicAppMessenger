package com.consultoraestrategia.messengeracademico.chat;

import android.content.Intent;
import android.view.View;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.actionMode.BasePresenterActionMode;
import com.consultoraestrategia.messengeracademico.base.actionMode.SelectListener;
import com.consultoraestrategia.messengeracademico.chat.events.ChatEvent;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatView;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatPresenter extends BasePresenterActionMode<ChatView, ChatMessage>{

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

    void onOfficialMessageActionClicked(ChatMessage message, View view);

    void officialMessageConfirmed(ChatMessage message);

    void officialMessageDenied(ChatMessage message);

    void onMessageNotReaded(ChatMessage message);

    void onMessageNotSended(ChatMessage message);

    void loadMoreMessages(ChatMessage olderMessage);

    void onActionViewContactSelected();

    void actionDelete();

    void actionCopy();

    void onImageClick(ChatMessage message);

    void attachVideoClicked();

    void attachAudioClicked();

    void attachLocationClicked();

    void attachContactClicked();

}
