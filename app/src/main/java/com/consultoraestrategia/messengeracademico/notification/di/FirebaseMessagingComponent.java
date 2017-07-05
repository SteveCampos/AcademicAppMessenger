package com.consultoraestrategia.messengeracademico.notification.di;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoAppModule;
import com.consultoraestrategia.messengeracademico.chat.di.ChatModule;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingPresenter;
import com.consultoraestrategia.messengeracademico.notification.MyFirebaseMessagingService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by @stevecampos on 30/06/2017.
 */

@Singleton
@Component(modules = {FirebaseMessagingModule.class, MessengerAcademicoAppModule.class})
public interface FirebaseMessagingComponent {

    void inject(MyFirebaseMessagingService service);

    FirebaseMessagingPresenter getPresenter();

}
