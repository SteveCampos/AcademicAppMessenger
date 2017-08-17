package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.domain.FirebaseLoadProfile;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageView;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by kike on 19/07/2017.
 */

public class ProfileEditImagePresenterImpl implements ProfileEditImagePresenter {

    private static final String TAG = ProfileEditImagePresenterImpl.class.getSimpleName();
    private ProfileEditImageView view;
    private EventBus eventBus;
    private FirebaseUser currentUser;
    private File cacheDir;
    private ContentResolver contentResolver;
    private Resources resources;


    public ProfileEditImagePresenterImpl(Resources resources, File cacheDir, ContentResolver contentResolver) {
        this.eventBus = GreenRobotEventBus.getInstance();
        this.resources = resources;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.cacheDir = cacheDir;
        this.contentResolver = contentResolver;
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (view != null) {
            view.startMainActivity();
        }
    }

    @Override
    public void attachView(BaseView view) {
        Log.d(TAG, "attachView");
        this.view = (ProfileEditImageView) view;
        checkCurrentUser();
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        eventBus.unregister(this);
        view = null;
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
    public void checkCurrentUser() {
        Log.d(TAG, "checkCurrentUser");
        if (currentUser != null) {
            showUser();
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        if (view != null) {
            view.startMainActivity();
        }
    }

    private void showUser() {
        if (view != null) {
            String displayName = currentUser.getDisplayName();
            Uri photoUri = currentUser.getPhotoUrl();
            String phoneNumber = currentUser.getPhoneNumber();
            Log.d(TAG, "displayName: " + displayName);
            Log.d(TAG, "photoUrl: " + photoUri);
            Log.d(TAG, "phoneNumber: " + phoneNumber);
            showUserName(displayName);
            showUserPhoto(photoUri);
            showUserPhonenumber(phoneNumber);
        }
    }

    private void showUserName(String displayName) {
        if (view != null) {
            view.showUserDisplayName(displayName);
        }
    }

    private void showUserPhoto(Uri photoUri) {
        if (view != null) {
            view.showUserPhoto(photoUri);
        }
    }

    private void showUserPhonenumber(String phoneNumber) {
        if (view != null) {
            view.showUserPhoneNumber(phoneNumber);
        }
    }

    @Override
    public void updateUserPhoto(Uri photoUri) {
        Log.d(TAG, "updateUserPhoto");
        uploadPhoto(photoUri);
    }

    private void uploadPhoto(Uri photoUri) {
        FirebaseLoadProfile.uploadUserPhoto(
                new User(currentUser.getUid(), null, null, null, photoUri, null),
                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri photoUri = taskSnapshot.getDownloadUrl();
                        updateUser(new User(currentUser.getUid(), null, currentUser.getPhoneNumber(), currentUser.getEmail(), photoUri, null));
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(R.string.load_profile_error);
                    }
                }
        );
    }

    //Actualizo los nodos del usuario
    private void updateUser(final User user) {
        FirebaseLoadProfile loadProfile = new FirebaseLoadProfile();
        loadProfile.updateUser(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    updateUserPhoto(user);
                } else {
                    showMessage(R.string.load_profile_error);
                }
            }
        });
    }

    //Actualizo al Usuario de Firebase
    private void updateUserPhoto(User user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(user.getPhotoUri())
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                            showMessage(R.string.user_photo_upload_sucess);
                        } else {
                            showMessage(R.string.load_profile_error);
                        }
                    }
                });
    }

    @MainThread
    @Subscribe
    @Override
    public void onEventMainThread(ProfileEditEvent loadProfileEvent) {
        Log.d(TAG, "onEventMainThread");
    }

    private void showMessage(@StringRes int message) {
        if (view != null) {
            view.showMessage(message);
        }
    }

    private void showProgress() {
        if (view != null) {
            view.showProgressBar();
        }
    }

    private void hideProgress() {
        if (view != null) {
            view.hideProgressBar();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == ResultCodes.OK) {
                Log.d(TAG, "uri: " + result.getUri());
                showUserPhoto(result.getUri());
                showProgress();
                compressImage(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(R.string.unknown_error);
            }
            //return;
        }

        //showMessage(R.string.unknown_response);
    }


    Uri selectedCompressedImageUri;

    private void compressImage(Uri imageUri) {
        ImageCompression imageCompression = new ImageCompression(cacheDir, contentResolver) {
            @Override
            protected void onPostExecute(MediaFile mediaFile) {
                Log.d(TAG, "selectedCompressedImageUri path: " + selectedCompressedImageUri);
                // image here is compressed & ready to be sent to the server
                selectedCompressedImageUri = mediaFile.getLocalUri();
                updateUserPhoto(selectedCompressedImageUri);
            }
        };
        imageCompression.execute(imageUri);// imagePath as a string
    }


}
