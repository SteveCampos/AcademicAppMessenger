package com.consultoraestrategia.messengeracademico.main.ui;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.base.actionMode.BaseActionModeView;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;

/**
 * Created by Steve on 7/03/2017.
 */

public interface MainView extends BaseActionModeView<MainPresenter, Chat>, ChatListListener{
    void startChat(Contact contact);

    void fireNotification(ChatMessage message);

    /*Check Google Services*/
    boolean checkGooglePlayServicesAvailable();

    void startImportDataJobService();

    void showToolbarProgress();

    void hideToolbarProgress();

    void reloadContacts();

    void updateChat(Chat chat);

    void removeChat(Chat chat);
}
