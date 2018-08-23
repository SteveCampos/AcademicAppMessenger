package com.consultoraestrategia.messengeracademico.profileEditImage;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.domain.FirebaseImageStorage;
import com.consultoraestrategia.messengeracademico.domain.FirebaseLoadProfile;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.entities.User;
import com.consultoraestrategia.messengeracademico.lib.EventBus;

import com.consultoraestrategia.messengeracademico.profileEditName.event.ProfileEditEvent;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.theartofdev.edmodo.cropper.CropImage;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;

/**
 * Created by kike on 19/07/2017.
 */

public class ProfileEditImagePresenterImpl extends BasePresenterImpl<ProfileEditImageView> implements ProfileEditImagePresenter {

    private static final String TAG = ProfileEditImagePresenterImpl.class.getSimpleName();
    private FirebaseUser currentUser;
    private File cacheDir;
    private ContentResolver contentResolver;

    public ProfileEditImagePresenterImpl(UseCaseHandler handler, Resources res, EventBus eventBus, FirebaseUser currentUser, File cacheDir, ContentResolver contentResolver) {
        super(handler, res, eventBus);
        this.currentUser = currentUser;
        this.cacheDir = cacheDir;
        this.contentResolver = contentResolver;
    }


    @Override
    public void attachView(ProfileEditImageView view) {
        Log.d(TAG, "attachView");
        super.attachView(view);
        checkCurrentUser();
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected BasePresenter<ProfileEditImageView> getPresenterImpl() {
        return this;
    }

    @Override
    public void checkCurrentUser() {
        Log.d(TAG, "checkCurrentUser");
        if (currentUser != null) {
            showUser();
        } else {
            showFinalMessage("No se encontró al usuario");
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
            Contact contact = Contact.getContact(currentUser.getUid());
            if (contact != null) {
                String infoVerified = contact.getInfoVerified();
                if (TextUtils.isEmpty(infoVerified)) {
                    infoVerified = res.getString(R.string.global_user_not_verificated);
                }
                showInfoVerified(infoVerified);
            }
        }
    }

    private void showInfoVerified(String infoVerified) {
        if (view != null) view.showInfoVerified(infoVerified);
    }


    private void showUserName(String displayName) {
        if (view != null) {
            view.showDisplayName(displayName);
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
        FirebaseLoadProfile.uploadUserPhoto(photoUri, new FirebaseImageStorage.FileListener() {
            @Override
            public void onSuccess(String url) {
                updateUser(new User(currentUser.getUid(), null, currentUser.getPhoneNumber(), currentUser.getEmail(), Uri.parse(url), null));
            }

            @Override
            public void onFailure(Exception exception) {
                showMessage(R.string.load_profile_error);
            }

            @Override
            public void onProgress(double progress) {

            }
        });
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: " + requestCode + ", resultCode: " + resultCode + ", data: " + data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == AppCompatActivity.RESULT_OK) {
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

    public static final int REQUEST_CODE_EDIT_NAME = 9000;

    @Override
    public void onBtnEditNameClicked() {
        Log.d(TAG, "onBtnEditNameClicked: ");
        showEdittextDialog(
                currentUser.getDisplayName(),
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
                res.getString(R.string.global_edit_name),
                currentUser.getDisplayName(),
                REQUEST_CODE_EDIT_NAME
        );
    }


    private Uri selectedCompressedImageUri;

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


    @Override
    public void onTextSubmit(final String nameSubmit, int requestCode) {
        Log.d(TAG, "onTextSubmit: " + nameSubmit);
        if (requestCode == REQUEST_CODE_EDIT_NAME) {
            User user = new User();
            user.setUid(currentUser.getUid());
            user.setDisplayName(nameSubmit);
            com.consultoraestrategia.messengeracademico.domain.FirebaseUser firebaseUser = new com.consultoraestrategia.messengeracademico.domain.FirebaseUser();
            firebaseUser.updateUser(
                    user,
                    new com.consultoraestrategia.messengeracademico.domain.FirebaseUser.UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                            showUserName(nameSubmit);
                            showMessage(R.string.global_message_success);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d(TAG, "onFailure: " + e);
                            showMessage(R.string.global_message_error);
                        }
                    }
            );
        }
    }
}
