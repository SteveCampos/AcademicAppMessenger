package com.consultoraestrategia.messengeracademico.loadProfile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenter;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenterImpl;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity.PREF_STEP_IMPORT_DATA;


public class LoadProfileActivity extends AppCompatActivity implements LoadProfileView {


    private static final String TAG = LoadProfileActivity.class.getSimpleName();
    public static final String PREF_STEP_LOAD_PROFILE = "PREF_STEP_LOAD_PROFILE";
    @BindView(R.id.txtSubtitle)
    AppCompatTextView txtSubtitle;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.edtName)
    AppCompatEditText edtName;
    @BindView(R.id.tilName)
    TextInputLayout tilName;
    @BindView(R.id.imgEmoji)
    AppCompatImageView imgEmoji;
    @BindView(R.id.activity_load_profile)
    RelativeLayout activityLoadProfile;

    @BindView(R.id.btnGo)
    AppCompatButton btnGo;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private FirebaseContactsHelper firebaseContactsHelper;
    private Uri imageUri;
    private LoadProfilePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile);
        ButterKnife.bind(this);
        //initViews();
        Log.d(TAG, "phone_number: " + getPhoneNumber());
        firebaseContactsHelper = new FirebaseContactsHelper();
        presenter = new LoadProfilePresenterImpl(this);
        presenter.onCreate();

    }

    private void initViews() {
        edtName.setText("Yo");
        //Glide.with(this).load("https://lh3.googleusercontent.com/-B42xVxBMpx0/AAAAAAAAAAI/AAAAAAAAEc8/aZ66s9K0gR4/s60-p-rw-no/photo.jpg").into(imgProfile);
    }


    public String getPhoneNumber() {
        String phoneNumber = null;
        Intent intent = getIntent();
        if (intent.hasExtra(VerificationActivity.EXTRA_PHONENUMBER)) {
            phoneNumber = intent.getStringExtra(VerificationActivity.EXTRA_PHONENUMBER);
        }
        if (phoneNumber == null) {
            phoneNumber = getPhoneNumberFromPreferences();
        }
        return phoneNumber;
    }


    private String getPhoneNumberFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
    }

    /*
        private void phoneNumberExists(final String phoneNumber) {
            firebaseContactsHelper.existPhoneNumber(phoneNumber, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        String key = dataSnapshot.getValue(String.class);
                        if (key != null) {
                            Contact contact = new Contact();
                            contact.setUserKey(key);
                            contact.setPhoneNumber(phoneNumber);
                            contact.setName(edtName.getText().toString());
                            contact.save();
                            saveStep();
                            forwardToImportData();
                        } else {
                            postPhoneNumber(phoneNumber);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showError();
                }
            });
        }

        private void postPhoneNumber(final String phoneNumber) {
            firebaseContactsHelper.postPhoneNumber(phoneNumber, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        showError();
                    } else {
                        phoneNumberExists(phoneNumber);
                    }
                }
            });
        }
    */
    @OnClick(R.id.btnGo)
    public void btnGo() {
        // final String phoneNumber = getPhoneNumberFromPreferences();
        String mName = edtName.getText().toString();
        onRegisterNewProfile(imageUri, mName, getPhoneNumber());

       /* if (phoneNumber != null) {
            phoneNumberExists(phoneNumber);


        } else {
            showError();
        }*/
    }


    private AppCompatActivity getActivity() {
        return this;
    }

    private void saveStep() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MainActivity.PREF_STEP, PREF_STEP_IMPORT_DATA);
        editor.apply();
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1).start(this);

    }


    @Override
    public void showLoadProfileViews() {
        requestFocus(edtName);
        tilName.setVisibility(View.VISIBLE);
        imgEmoji.setVisibility(View.VISIBLE);
        btnGo.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideLoadProfileViews() {
        tilName.setVisibility(View.GONE);
        imgEmoji.setVisibility(View.GONE);
        btnGo.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgProfile)
    @Override
    public void showDialog() {
        startCropImageActivity(null);
    }

    @Override
    public void onUpladProfileError(String error) {
        showSnackbar(error);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgProfile.setImageURI(result.getUri());
                imageUri = result.getUri();
                Log.d(TAG, "ResulURi: " + result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d(TAG, "Cropping failed: " + result.getError());
            }
        }
    }

    @Override
    public void onRegisterNewProfile(Uri myUri, String mName, String mPhoneNumber) {
        Log.d(TAG, "myUri: " + myUri);
        presenter.updateProfile(myUri, mName, mPhoneNumber);
    }

    @Override
    public void onRegisterNewProfileError(String message) {
        showSnackbar(message);
    }

    @Override
    public void onProfileVerificated(String phoneNumber) {
        if (phoneNumber != null) {
            presenter.profileVerificated(phoneNumber);
        } else {
            Log.d(TAG, "NewProfile");
        }
    }

    @Override
    public void forwardToImportData(Profile profile) {
        Log.d(TAG, "forwardToImportData");
        if (profile != null) {
            try {

                Contact contact = new Contact();
                contact.setUserKey(profile.getUserKey());
                contact.setPhoneNumber(profile.getmPhoneNumber());
                contact.setName(profile.getmName());
                contact.setPhotoUri(profile.getPhoto().getUrl());
                contact.save();

                saveStep();

            } catch (Exception ex) {
                Log.d(TAG, "ex: " + ex);
                onUpladProfileError(ex.getMessage());
                return;
            }


            goToImport();

        } else {

            onUpladProfileError("profile null");
        }
    }


    private void requestFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void showSnackbar(CharSequence message) {
        Snackbar.make(imgEmoji, message, Snackbar.LENGTH_LONG).show();
    }


    public void goToImport() {
        Intent intent = new Intent(this, ImportDataActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}