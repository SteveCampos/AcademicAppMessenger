package com.consultoraestrategia.messengeracademico.loadProfile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
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

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenter;
import com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenterImpl;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.firebase.ui.auth.AuthUI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity.PREF_STEP_IMPORT_DATA;
import static com.consultoraestrategia.messengeracademico.loadProfile.LoadProfilePresenterImpl.RC_SIGN_IN;


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
    RelativeLayout rootView;

    @BindView(R.id.btnGo)
    AppCompatButton btnGo;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private LoadProfilePresenter presenter;

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile);
        ButterKnife.bind(this);
        setupInjection();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if (presenter != null) {
            presenter.onStart();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    private void setupInjection() {
        Log.d(TAG, "setupInjection");
        presenter = (LoadProfilePresenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = new LoadProfilePresenterImpl(getResources(), getCacheDir(), getContentResolver());
            presenter.onCreate();
        }
        presenter.attachView(this);
    }

    @OnClick(R.id.btnGo)
    public void btnGo() {
        String name = edtName.getText().toString();
        presenter.updateUser(name);
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
    public void startSelectImageActivity() {
        startCropImageActivity(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void showMessage(@StringRes int message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startAuthActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void showErrorUpdateUser(String message) {
        showSnackbar(message);
    }

    @Override
    public void showUserCurrentDisplayName(String displayName) {
        Log.d(TAG, "showUserCurrentDisplayName");
        edtName.setText(displayName);
    }

    @Override
    public void showUserCurrentPhoto(Uri photoUri) {
        Log.d(TAG, "showUserCurrentPhoto");
        Glide.with(this)
                .load(photoUri)
                .fitCenter()
                .into(imgProfile);
    }


    @Override
    public void startImportDataActivity() {
        saveStep();
        Intent intent = new Intent(this, ImportDataActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }


    private void showSnackbar(CharSequence message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void setPresenter(LoadProfilePresenterImpl presenter) {

    }
}
