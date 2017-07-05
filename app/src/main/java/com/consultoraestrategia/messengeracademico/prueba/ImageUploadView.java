package com.consultoraestrategia.messengeracademico.prueba;

import android.net.Uri;

/**
 * Created by @stevecampos on 13/06/2017.
 */

public interface ImageUploadView {
    void showImage(Uri uri);

    void showError(String error);

    void showProgress(double percent);

    void hideProgress();

    void startCropImageActivity(Uri uri);

}
