package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.content.Intent;
import android.net.Uri;

import com.consultoraestrategia.messengeracademico.BasePresenter;
import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;
import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;

/**
 * Created by kike on 19/07/2017.
 */

public interface ProfileEditImagePresenter extends BasePresenter {
    /*
    void onDestroy();
    void onCreate();
    void initInputsView();
    void editProfileImage(String name,String phoneNumber,Uri uriParse);
    void onEventMainThread(ProfileEditEvent profileEditEvent);*/

    void checkCurrentUser();

    void updateUserPhoto(Uri photoUri);

    void onEventMainThread(ProfileEditEvent loadProfileEvent);

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
