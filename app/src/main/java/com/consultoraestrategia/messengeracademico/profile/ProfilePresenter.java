package com.consultoraestrategia.messengeracademico.profile;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.profile.event.ProfileEvent;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileView;

/**
 * Created by kike on 3/06/2017.
 */

public interface ProfilePresenter extends BasePresenter<ProfileView> {
    void onPhoneNumberMenuClicked();

    void actionEditClicked();

    void actionViewContactClicked();

// void onEventMainThread(ProfileEvent profileEvent);
}
