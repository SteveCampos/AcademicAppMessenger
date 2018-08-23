package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.net.Uri;
import android.support.annotation.StringRes;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenter;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenterImpl;

/**
 * Created by kike on 17/07/2017.
 */

public interface ProfileEditImageView extends BaseView<ProfileEditImagePresenter> {


    void showDisplayName(String displayName);

    void showUserPhoto(Uri photoUri);

    void showInfoVerified(String verified);

    void showUserPhoneNumber(String phoneNumber);


    void startSelectImageActivity();

}
