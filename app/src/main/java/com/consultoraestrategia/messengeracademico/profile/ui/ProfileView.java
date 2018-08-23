package com.consultoraestrategia.messengeracademico.profile.ui;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenter;

/**
 * Created by kike on 3/06/2017.
 */

public interface ProfileView extends BaseView<ProfilePresenter> {

    void showName(String name);

    void showPhonenumber(String phonenumber);

    void showTextVerified(String textVerified);

    void showRegularUserWithoutVerification();

    void showPhoto(String url);

    void showEmptyPhoto();

    void editContactInPhone(String phonenumber);

    void showContactInPhone(String phonenumber);

    void launchChatActivity(String phonenumberTo);
}
