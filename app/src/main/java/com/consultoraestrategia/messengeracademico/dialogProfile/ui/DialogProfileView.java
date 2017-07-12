package com.consultoraestrategia.messengeracademico.dialogProfile.ui;

/**
 * Created by kike on 10/07/2017.
 */

public interface DialogProfileView {
    void onVerificatedProfileDialog(String name,String phoneNumber);
    void onImageUri(String imageUri);
    void onImageUriNull();
    void onVerificatedProfileDialogError(String error);
    void initView();
//    void startChat();
//    void startProfileInfo();
}
