package com.consultoraestrategia.messengeracademico.loadProfile.ui;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.entities.Profile;

/**
 * Created by kike on 24/02/2017.
 */

public interface LoadProfileView {
    void showLoadProfileViews();

    void hideLoadProfileViews();

    void showProgress();

    void hideProgress();

    /*List Items Select-Options*/
    void showDialog();

    void onUpladProfileError(String error);

    //void onRegisterNewProfile(Uri myUri, String mName, String mPhoneNumber);
    void onRegisterNewProfile(Uri myUri, String mName, String mPhoneNumber);

    void onRegisterNewProfileError(String message);

    /*Validate key && String UriUrl*/
    void onProfileVerificated(String phoneNumber);

    void forwardToImportData(Profile profile);

    //void setData(String name);
}
