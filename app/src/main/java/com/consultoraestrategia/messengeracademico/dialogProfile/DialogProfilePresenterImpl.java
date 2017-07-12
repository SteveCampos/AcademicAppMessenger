package com.consultoraestrategia.messengeracademico.dialogProfile;

import com.consultoraestrategia.messengeracademico.dialogProfile.ui.DialogProfileView;


/**
 * Created by kike on 10/07/2017.
 */

public class DialogProfilePresenterImpl implements DialogProfilePresenter {
    private DialogProfileView view;

    public DialogProfilePresenterImpl(DialogProfileView view) {
        this.view = view;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void verificatedDialogProfile(String name , String phoneNumber) {
        if (name != null  && phoneNumber != null) {
            view.initView();
        } else {
            view.onVerificatedProfileDialogError("Error String");
        }
    }

    @Override
    public void validImageUri(String imageUri) {
        if (imageUri!=null){
            view.onImageUri(imageUri);
        }else{
            view.onImageUriNull();
        }
    }

}
