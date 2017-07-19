package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;

/**
 * Created by kike on 19/07/2017.
 */

public interface ProfileEditImagePresenter {
    void onDestroy();
    void onCreate();
    void initInputsView();
    void editProfileImage(String name,String phoneNumber,Uri uriParse);
    void onEventMainThread(ProfileEditEvent profileEditEvent);
}
