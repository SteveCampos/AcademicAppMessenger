package com.consultoraestrategia.messengeracademico.dialogProfile;

/**
 * Created by kike on 10/07/2017.
 */

public interface DialogProfilePresenter {
    void onDestroy();
    void onCreate();
    void verificatedDialogProfile(String name , String phoneNumber);
    void validImageUri (String imageUri);
}
