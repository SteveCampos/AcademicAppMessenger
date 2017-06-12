package com.consultoraestrategia.messengeracademico.chat.di;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoAppModule;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by @stevecampos on 9/06/2017.
 */

@Singleton
@Component(modules = {ChatModule.class, MessengerAcademicoAppModule.class})
public interface ChatComponent {

    void inject(ChatActivity activity);

    ChatPresenter getPresenter();

    ChatMessageAdapter getAdapter();
}
