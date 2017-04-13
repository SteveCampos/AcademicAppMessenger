package com.consultoraestrategia.messengeracademico.loadProfile;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;


/**
 * Created by kike on 24/02/2017.
 */

    public interface LoadProfilePresenter {
    void onDestroy();
    void onCreate();
    void initInputsView();
    void updateProfile(Uri uriPhotoProfile, String mName, String mPhoneNumber);
    void profileVerificated(String phoneNumber);


    void onEventMainThread(LoadProfileEvent loadProfileEvent);
}
