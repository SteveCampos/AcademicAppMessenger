package com.consultoraestrategia.messengeracademico.main.di;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoAppModule;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by @stevecampos on 31/05/2017.
 */

@Singleton
@Component(modules = {MainModule.class, MessengerAcademicoAppModule.class})
public interface MainComponent {
    void inject(MainActivity activity);

    MainPresenter getPresenter();

    MyFragmentAdapter getAdapter();
}
