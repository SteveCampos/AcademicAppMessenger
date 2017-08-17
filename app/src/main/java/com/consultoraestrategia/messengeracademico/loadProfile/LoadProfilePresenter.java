package com.consultoraestrategia.messengeracademico.loadProfile;

import android.content.Intent;
import android.net.Uri;

import com.consultoraestrategia.messengeracademico.BasePresenter;
import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;


/**
 * Created by kike on 24/02/2017.
 */

public interface LoadProfilePresenter extends BasePresenter {

    void checkCurrentUser();

    void updateUser(String name);

    void onEventMainThread(LoadProfileEvent loadProfileEvent);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
