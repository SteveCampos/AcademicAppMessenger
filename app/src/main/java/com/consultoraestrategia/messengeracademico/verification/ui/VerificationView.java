package com.consultoraestrategia.messengeracademico.verification.ui;

import com.consultoraestrategia.messengeracademico.entities.PhoneNumberVerified;

/**
 * Created by Steve on 17/02/2017.
 */

public interface VerificationView {

    void setPhoneNumber(String phoneNumber);

    void showVerificationViews();
    void hideVerificationViews();

    void showDialog(String phoneNumber);

    void showWatingViews(String phoneNumber);
    void hideWatingViews();

    void showProgress();
    void hideProgress();

    void listenMessages();
    void setTextCodeChangeListener();
    void setCode(String code);

    /*SUCESS*/
    void onPhoneNumberVerificated(PhoneNumberVerified numberVerified);


    /*ERRORS*/
    void onInvalidPhoneNumber();
    void onInvalidCode();

    void OnPhoneNumberSendedError(String error);
    void onPhoneNumberFailedToVerificated(String error);

    void onMessageReceived(String sender, String messageText);

    /*Check Google Services*/
    boolean  checkGooglePlayServicesAvailable();

}
