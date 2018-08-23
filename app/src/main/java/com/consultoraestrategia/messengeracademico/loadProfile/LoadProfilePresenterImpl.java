package com.consultoraestrategia.messengeracademico.loadProfile;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.domain.FirebaseImageStorage;
import com.consultoraestrategia.messengeracademico.domain.FirebaseLoadProfile;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.loadProfile.event.LoadProfileEvent;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileView;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.theartofdev.edmodo.cropper.CropImage;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;


/**
 * Created by kike on 24/02/2017.
 */

public class LoadProfilePresenterImpl implements LoadProfilePresenter {
    public static final int RC_SIGN_IN = 100;
    private static final String TAG = LoadProfilePresenterImpl.class.getSimpleName();
    private LoadProfileView view;
    private LoadProfileInteractor interactor;
    private EventBus eventBus;
    private Resources resources;
    private FirebaseUser currentUser;
    private File cacheDir;
    private ContentResolver contentResolver;

    public LoadProfilePresenterImpl(Resources resources, File cacheDir, ContentResolver contentResolver) {
        this.interactor = new LoadProfileInteractorImpl();
        this.eventBus = new GreenRobotEventBus();
        this.resources = resources;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.cacheDir = cacheDir;
        this.contentResolver = contentResolver;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
    }

    @Override
    public void setExtras(Bundle extras) {

    }

    @Override
    public void attachView(BaseView view) {
        Log.d(TAG, "attachView");
        this.view = (LoadProfileView) view;
        checkCurrentUser();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");

    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        view = null;
        eventBus.unregister(this);
    }

    private boolean isValideName(String name) {
        return name != null && !name.isEmpty() && !name.trim().isEmpty() && !(name.length() > 16);
    }

    private void showFieldsError() {
        showError(R.string.load_profile_error_fields);
    }

    private void showError(@StringRes int error) {
        view.hideProgress();
        view.showMessage(error);
    }


    @Override
    public void checkCurrentUser() {
        Log.d(TAG, "checkCurrentUser");
        if (currentUser != null) {
            showCurrentUser();
        } else {
            startAuthActivity();
        }
    }

    @Override
    public void updateUser(final String name) {

        if (view != null) {
            view.showProgress();
        }

        if (!isValideName(name)) {
            showFieldsError();
            return;
        }

        User updatedUser = new User
                (
                        currentUser.getUid(),
                        name,
                        currentUser.getPhoneNumber(),
                        currentUser.getEmail(),
                        currentUser.getPhotoUrl(),
                        ""
                );


        if (selectedCompressedImageUri != null) {
            //Seleccionó alguna imagen
            //Primero subo la imagen, y luego actualizao al usuario
            updatedUser.setPhotoUri(selectedCompressedImageUri);
            FirebaseLoadProfile.uploadUserPhoto(selectedCompressedImageUri, new FirebaseImageStorage.FileListener() {
                @Override
                public void onSuccess(String url) {
                    updateUser(new User(currentUser.getUid(), name, currentUser.getPhoneNumber(), currentUser.getEmail(), Uri.parse(url), null));
                }

                @Override
                public void onFailure(Exception exception) {
                    showMessage(R.string.load_profile_error);
                }

                @Override
                public void onProgress(double progress) {

                }
            });
        } else {
            //No seleccionó ninguna imagen
            //Actualizo al usuario sin subir la imagen
            updateUser(updatedUser);
        }
    }


    //Actualizo los nodos del usuario
    private void updateUser(final User user) {
        FirebaseLoadProfile loadProfile = new FirebaseLoadProfile();
        loadProfile.updateUser(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    updateFirebaseUser(user);
                } else {
                    showError(R.string.load_profile_error);
                }
            }
        });
    }

    //Actualizo al Usuario de Firebase
    private void updateFirebaseUser(User user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(user.getDisplayName())
                .setPhotoUri(user.getPhotoUri())
                .build();
        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startImportData();
                        } else {
                            showError(R.string.load_profile_error);
                        }
                    }
                });
    }

    private void startImportData() {
        if (view != null) {
            view.hideProgress();
            view.startImportDataActivity();
        }
    }


    private void startAuthActivity() {
        Log.d(TAG, "startAuthActivity");
        if (view != null) {
            view.startAuthActivity();
        }
    }

    private void showCurrentUser() {
        if (view != null) {
            String displayName = currentUser.getDisplayName();
            Uri photoUri = currentUser.getPhotoUrl();
            Log.d(TAG, "displayName: " + displayName);
            Log.d(TAG, "photoUrl: " + photoUri);
            showViews();
            showUserDisplayName(displayName);
            showUserPhoto(photoUri);
        }
    }

    private void showViews() {
        view.showLoadProfileViews();
    }

    private void showUserDisplayName(String displayName) {
        if (displayName != null && !TextUtils.isEmpty(displayName)) {
            view.showUserCurrentDisplayName(displayName);
        }
    }

    private void showUserPhoto(Uri photoUri) {
        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            view.showUserCurrentPhoto(photoUri);
        }
    }

    @Subscribe
    @Override
    public void onEventMainThread(LoadProfileEvent loadProfileEvent) {
        switch (loadProfileEvent.getType()) {
            case LoadProfileEvent.OnProfileUploadSuccess:
                //onProfileUploadSuccess(loadProfileEvent.getProfile());
                break;
            case LoadProfileEvent.OnProfileUploadError:
                //onProfileUploadError(loadProfileEvent.getError());
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log.d(TAG, "uri: " + result.getUri());
                showUserPhoto(result.getUri());
                compressImage(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(R.string.unknown_error);
            }
        }

    }

    Uri selectedCompressedImageUri;

    private void compressImage(Uri imageUri) {
        ImageCompression imageCompression = new ImageCompression(cacheDir, contentResolver) {
            @Override
            protected void onPostExecute(MediaFile mediaFile) {
                selectedCompressedImageUri = mediaFile.getLocalUri();
                // image here is compressed & ready to be sent to the server
                Log.d(TAG, "selectedCompressedImageUri path: " + selectedCompressedImageUri);
            }
        };
        imageCompression.execute(imageUri);// imagePath as a string
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == AppCompatActivity.RESULT_OK) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(TAG, "phoneNumber: " + response.getPhoneNumber());
            checkCurrentUser();
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showErrorSignin(R.string.sign_in_cancelled);
                return;
            }

            if (response.getError() == null) return;

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showErrorSignin(R.string.no_internet_connection);
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showErrorSignin(R.string.unknown_error);
                return;
            }
        }

        showErrorSignin(R.string.unknown_sign_in_response);
    }

    private void showMessage(@StringRes int resource) {
        if (view != null) {
            view.showMessage(resource);
        }
    }

    private void showErrorSignin(@StringRes int error) {
        if (view != null) {
            view.showErrorSingin(error);
        }
    }

}
