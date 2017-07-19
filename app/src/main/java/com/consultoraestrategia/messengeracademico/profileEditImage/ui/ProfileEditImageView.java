package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 17/07/2017.
 */

public interface ProfileEditImageView {

    void showProgressBar();

    void hideProgressBar();

    void onProfileEditImageError(String message);

    void forwardProfileData(Profile profile);

    void showProfileEditViews();


}
