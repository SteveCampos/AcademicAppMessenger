package com.consultoraestrategia.messengeracademico.loadProfile.ui;

import android.net.Uri;
import android.support.annotation.StringRes;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenterImpl;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by kike on 24/02/2017.
 */

public interface LoadProfileView extends BaseView<LoadProfilePresenterImpl> {
    void showLoadProfileViews();

    void hideLoadProfileViews();

    void showProgress();

    void hideProgress();

    void startAuthActivity();

    void showErrorUpdateUser(String message);

    void showUserCurrentDisplayName(String displayName);

    void showUserCurrentPhoto(Uri photoUri);

    void startImportDataActivity();

    void startSelectImageActivity();

    void showMessage(@StringRes int message);

    /*void showDialog();

    void onRegisterNewProfile(Uri myUri, String mName, String mPhoneNumber);

    void onRegisterNewProfileError(String message);

    void forwardToImportData(Profile profile);*/

}
