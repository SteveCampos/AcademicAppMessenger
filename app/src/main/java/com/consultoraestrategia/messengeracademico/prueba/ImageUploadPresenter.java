package com.consultoraestrategia.messengeracademico.prueba;

import android.content.Intent;

/**
 * Created by @stevecampos on 13/06/2017.
 */

public interface ImageUploadPresenter {

    void setView(ImageUploadView view);

    void pickImage();

    void onCropImageActivityResult(int requestCode, int resultCode, Intent data);
}
