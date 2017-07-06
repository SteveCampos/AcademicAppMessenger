package com.consultoraestrategia.messengeracademico.profile;

import com.consultoraestrategia.messengeracademico.profile.event.ProfileEvent;

/**
 * Created by kike on 3/06/2017.
 */

public interface ProfilePresenter {
    void onDestroy();
    void onCreate();
    void initInputsView();
    void verificatedProfileEdit(String phoneNumber);
    void verificatedProfileInformation(String phoneNumber);

    void onEventMainThread(ProfileEvent profileEvent);
}
