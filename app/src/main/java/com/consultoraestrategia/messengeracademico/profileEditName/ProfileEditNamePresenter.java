package com.consultoraestrategia.messengeracademico.profileEditName;

import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;

/**
 * Created by kike on 18/07/2017.
 */

public interface ProfileEditNamePresenter {

    void onDestroy();
    void onCreate();
    void initInputsView();
    void editProfileName(String name,String phoneNumber,String Uri);
    void onEventMainThread(ProfileEditEvent profileEditEvent);


}
