package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.base.BaseActivity;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenter;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenterImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kike on 17/07/2017.
 */

public class ProfileEditImageActivity extends BaseActivity<ProfileEditImageView, ProfileEditImagePresenter> implements ProfileEditImageView {

    public static String TAG = ProfileEditImageActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgProfile)
    CircleImageView imgPhoto;
    @BindView(R.id.txt_nameUser)
    TextView txtUsername;
    @BindView(R.id.txt_phoneNumber)
    TextView txtPhonenumber;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.relative)
    RelativeLayout relativeLayoutButtonImage;
    @BindView(R.id.txt_info_verified)
    TextView txtInfoVerified;

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected ProfileEditImagePresenter getPresenter() {
        return new ProfileEditImagePresenterImpl(
                new UseCaseHandler(new UseCaseThreadPoolScheduler()),
                getResources(),
                GreenRobotEventBus.getInstance(),
                FirebaseAuth.getInstance().getCurrentUser(),
                getCacheDir(),
                getContentResolver());
    }

    @Override
    protected ProfileEditImageView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_edit_profile_image);
        ButterKnife.bind(this);
        setupToolbar();
    }

    @Override
    protected ViewGroup getRootLayout() {
        return toolbar;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progressBar;
    }


    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }
    }

    @Override
    public void showDisplayName(String displayName) {
        Log.d(TAG, "showDisplayName");
        txtUsername.setText(displayName);
    }

    @Override
    public void showUserPhoto(Uri photoUri) {
        Log.d(TAG, "showUserPhoto");
        if (photoUri == null || photoUri.equals(Uri.EMPTY)) return;
        Glide
                .with(this)
                .load(photoUri)
                .error(R.drawable.ic_users)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imgPhoto);
    }

    @Override
    public void showInfoVerified(String verified) {
        Log.d(TAG, "showInfoVerified: " + verified);
        txtInfoVerified.setText(verified);
    }

    @Override
    public void showUserPhoneNumber(String phoneNumber) {
        Log.d(TAG, "showUserPhoneNumber");
        txtPhonenumber.setText(phoneNumber);
    }


    @OnClick(R.id.relative)
    @Override
    public void startSelectImageActivity() {
        Log.d(TAG, "startSelectImageActivity");
        startCropImageActivity(null);
        hideSoftboard();
    }

    private void hideSoftboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btn_edit)
    public void onViewClicked() {
        presenter.onBtnEditNameClicked();
    }

    @Override
    public void onTextSubmit(String text, int requestCode) {
        super.onTextSubmit(text, requestCode);
        presenter.onTextSubmit(text, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
