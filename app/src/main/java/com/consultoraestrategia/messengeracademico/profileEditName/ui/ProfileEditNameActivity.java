package com.consultoraestrategia.messengeracademico.profileEditName.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.profileEditName.ProfileEditNamePresenter;
import com.consultoraestrategia.messengeracademico.profileEditName.ProfileEditNamePresenterImpl;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kike on 18/07/2017.
 */

public class ProfileEditNameActivity extends AppCompatActivity implements ProfileEditNameView {
    private static String TAG = ProfileEditNameActivity.class.getSimpleName();
    @BindView(R.id.edtName)
    AppCompatEditText edtName;
    ProfileEditNamePresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_name);
        ButterKnife.bind(this);
        setToolbar();
        presenter = new ProfileEditNamePresenterImpl(this, this);
        presenter.onCreate();
    }


    public void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Escribe tu nombre");
        } else {
            Log.d(getLocalClassName(), "setToolbar null");
        }
    }


    @OnClick(R.id.button_ok)
    public void onClickOk() {
        String mName = edtName.getText().toString();
        presenter.editProfileName(mName, phoneNumber, photouri);
        hideSoftInput();
        Log.d(TAG, "Presenter");
    }

    @OnClick(R.id.button_cancelar)
    public void onClickCancelar() {
        Intent intent = new Intent(this, ProfileEditImageActivity.class);
        intent.putExtra("editNameUser", name);
        intent.putExtra("editPhoneNumber", phoneNumber);
        intent.putExtra("editphotoUri", photouri);
        startActivity(intent);
        hideSoftInput();
    }


    @Override
    public void onProfileEditError(String message) {
        Snackbar.make(edtName, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void forwardProfileData(Profile profile) {
        Log.d(TAG, "forwardProfileData");
        Intent intent = new Intent(this, ProfileEditImageActivity.class);
        intent.putExtra("editNameUser", profile.getmName());
        intent.putExtra("editPhoneNumber", profile.getmPhoneNumber());
        intent.putExtra("editphotoUri", profile.getPhoto().getUrl());
        startActivity(intent);

    }

    @Override
    public void initView() {
        manageIntent(getIntent());
    }

    @Override
    public void onProfileEditNameUploadError() {
        Snackbar.make(edtName, "Espere Conexión", Snackbar.LENGTH_LONG).show();
    }

    String name, phoneNumber, photouri;

    private void manageIntent(Intent intent) {
        if (intent.hasExtra("nameUser") && intent.hasExtra("phoneNumber") && intent.hasExtra("photoUri")) {
            name = intent.getStringExtra("nameUser");
            phoneNumber = intent.getStringExtra("phoneNumber");
            photouri = intent.getStringExtra("photoUri");
            initiViews();
        } else if (intent.hasExtra("nameUser") && intent.hasExtra("phoneNumber")) {
            name = intent.getStringExtra("nameUser");
            phoneNumber = intent.getStringExtra("phoneNumber");
            initiViews();
        }
    }

    private void initiViews() {

        edtName.setText(name);
        requestFocus(edtName);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void hideSoftInput() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void requestFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
