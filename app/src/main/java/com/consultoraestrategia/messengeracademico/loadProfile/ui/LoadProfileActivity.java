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

import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenter;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenterImpl;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
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
        Log.d(TAG, "phone_number: " + getPhoneNumber());
        firebaseContactsHelper = new FirebaseContactsHelper();
        presenter = new LoadProfilePresenterImpl(this, this);
        presenter.onCreate();

    }

    private void initViews() {
        edtName.setText("Yo");
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

    @OnClick(R.id.btnGo)
    public void btnGo() {
        String mName = edtName.getText().toString();
        onRegisterNewProfile(compreUri, mName, getPhoneNumber());
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
        //requestFocus(edtName);
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
        btnGo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        btnGo.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.imgProfile)
    @Override
    public void showDialog() {
        startCropImageActivity(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

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
                compressImage(imageUri);
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
                saveStep();
            } catch (Exception ex) {
                Log.d(TAG, "ex: " + ex);
                onUpladProfileError(ex.getMessage());
                return;
            }


            goToImport(profile);

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


    public void goToImport(Profile profile) {
        Intent intent = new Intent(this, ImportDataActivity.class);
        intent.putExtra(ImportDataActivity.EXTRA_NAME, profile.getmName());
        intent.putExtra(ImportDataActivity.EXTRA_PHONENUMBER, getPhoneNumber());
        intent.putExtra(ImportDataActivity.EXTRA_PHOTO_URI, profile.getPhoto().getUrl());
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    Uri compreUri;

    private void compressImage(Uri imageUri) {
        ImageCompression imageCompression = new ImageCompression(this.getCacheDir(), this.getContentResolver()) {
            @Override
            protected void onPostExecute(MediaFile mediaFile) {

                compreUri = mediaFile.getLocalUri();
                // image here is compressed & ready to be sent to the server
                Log.d(TAG, "imageCompression path: " + compreUri);
            }
        };
        imageCompression.execute(imageUri);// imagePath as a string
    }


}
