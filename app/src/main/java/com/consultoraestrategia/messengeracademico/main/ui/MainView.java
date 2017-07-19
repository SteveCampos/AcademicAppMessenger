package com.consultoraestrategia.messengeracademico.main.ui;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.MainPresenterImpl;

/**
 * Created by Steve on 7/03/2017.
 */

public interface MainView extends BaseView<MainPresenterImpl> {
    void startChat(Contact contact);

    void fireNotification(ChatMessage message);
}
