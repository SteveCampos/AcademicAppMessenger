package com.consultoraestrategia.messengeracademico.main;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.base.actionMode.BasePresenterActionMode;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.main.ui.MainView;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by jairc on 27/03/2017.
 */

public interface MainPresenter extends BasePresenterActionMode<MainView, Chat> {

    void listenForMessages();

    void suscribeToNotifications(FirebaseUser mainUser);

    void onActionImportClicked();

    void onActionDeleteClicked();
}
