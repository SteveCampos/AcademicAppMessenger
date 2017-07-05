package com.consultoraestrategia.messengeracademico.prueba;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.prueba.domain.usecase.UploadImage;
import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Created by @stevecampos on 13/06/2017.
 */

public class ImageUploadPresenterImpl implements ImageUploadPresenter {

    private static final String TAG = ImageUploadPresenterImpl.class.getSimpleName();

    private ImageUploadView view;
    private final UseCaseHandler useCaseHandler;
    private final EventBus eventBus;
    private UploadImage uploadImage;

    public ImageUploadPresenterImpl(UseCaseHandler useCaseHandler, EventBus eventBus, UploadImage uploadImage) {
        this.useCaseHandler = useCaseHandler;
        this.eventBus = eventBus;
        this.uploadImage = uploadImage;
    }

    public void setView(ImageUploadView view) {
        this.view = view;
    }

    @Override
    public void pickImage() {
        if (view != null) {
            view.startCropImageActivity(null);
        }
    }

    @Override
    public void onCropImageActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {

                Uri resultUri = result.getUri();
                //beginToUploadImage(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                showError(error);
            }
        }
    }

    /*
    private void beginToUploadImage(Uri uri) {
        if (view != null) {
            view.showProgress(0.0);
            view.showImage(uri);

            useCaseHandler.execute(
                    uploadImage,
                    new UploadImage.RequestValues(uri),
                    new UseCase.UseCaseCallback<UploadImage.ResponseValue>() {
                        @Override
                        public void onSuccess(UploadImage.ResponseValue response) {
                            int type = response.getType();
                            Log.d(TAG, "type: " + type);
                            switch (type) {
                                case UploadImage.SUCCESS:
                                    onImageUploadSucess(response.getUri());
                                    break;
                                case UploadImage.PROGRESS:
                                    onImageUploadProgress(response.getPercent());
                                    break;
                                case UploadImage.ERROR:
                                    onImageUploadFailure(response.getError());
                                    break;

                            }
                        }

                        @Override
                        public void onError() {
                            showError(new Exception("UseCaseHandler error!"));
                        }
                    }
            );
        }
    }*/

    private void onImageUploadSucess(Uri uri) {
        if (view != null) {
            view.hideProgress();
            view.showImage(uri);
        }
    }

    private void onImageUploadProgress(double percent) {
        if (view != null) {
            view.showProgress(percent);
        }
    }

    private void onImageUploadFailure(String error) {
        if (view != null) {
            view.showProgress(0.0);
            view.showError(error);
        }
    }


    private void showImage(Uri uri) {
        if (view != null) {
            view.showImage(uri);
        }
    }

    private void showError(Exception e) {
        if (view != null) {
            view.showError(e.getLocalizedMessage());
        }
    }
}
