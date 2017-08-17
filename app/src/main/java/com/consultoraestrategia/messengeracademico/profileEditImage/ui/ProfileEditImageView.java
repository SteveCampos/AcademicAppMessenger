package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.net.Uri;
import android.support.annotation.StringRes;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenterImpl;

/**
 * Created by kike on 17/07/2017.
 */

public interface ProfileEditImageView extends BaseView<ProfileEditImagePresenterImpl> {

    void showProgressBar();

    void hideProgressBar();

    /*void onProfileEditImageError(String message);

    void forwardProfileData(Profile profile);

    void showProfileEditViews();*/

    void showUserDisplayName(String displayName);

    void showUserPhoto(Uri photoUri);

    void showUserPhoneNumber(String phoneNumber);

    void startSelectImageActivity();

    void showMessage(@StringRes int message);

    void startMainActivity();


}
