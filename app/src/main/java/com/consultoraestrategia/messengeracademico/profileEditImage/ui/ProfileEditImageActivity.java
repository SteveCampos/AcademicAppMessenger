package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenter;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenterImpl;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kike on 17/07/2017.
 */

public class ProfileEditImageActivity extends AppCompatActivity implements ProfileEditImageView {

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

    private ProfileEditImagePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);
        ButterKnife.bind(this);
        setupToolbar();
        setupPresenter();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.profile_title);
        }
    }

    private void setupPresenter() {
        presenter = (ProfileEditImagePresenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = new ProfileEditImagePresenterImpl(getResources(), getCacheDir(), getContentResolver());
            presenter.onCreate();
        }
        presenter.attachView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    /*
    public void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Perfil");
        } else {
            Log.d(getLocalClassName(), "setToolbar null");
        }
    }


    String celpho, name, imageUrl;

    private void manageIntent(Intent intent) {
        if (intent.hasExtra(ImportDataActivity.EXTRA_PHONENUMBER)) {
            celpho = intent.getStringExtra(ImportDataActivity.EXTRA_PHONENUMBER);
            imageUrl = getContact(celpho).getPhotoUrl();
            name = getContact(celpho).getName();
            getContact(celpho);
            phoneNumber.setText(celpho);
            nameUser.setText(name);
        } else if (intent.hasExtra("editNameUser") && intent.hasExtra("editPhoneNumber") && intent.hasExtra("editphotoUri")) {
            name = intent.getStringExtra("editNameUser");
            celpho = intent.getStringExtra("editPhoneNumber");
            imageUrl = intent.getStringExtra("editphotoUri");
            getContact(celpho);
            nameUser.setText(name);
        } else {
            Log.d(TAG, "manageIntent null");
        }
    }

    private Contact getContact(String phoneNumber) {
        return SQLite.select()
                .from(Contact.class)
                .where(Contact_Table.phoneNumber.eq(phoneNumber))
                .querySingle();
    }*/

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        presenter.onBackPressed();
    }


    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showUserDisplayName(String displayName) {
        Log.d(TAG, "showUserDisplayName");
        txtUsername.setText(displayName);
    }

    @Override
    public void showUserPhoto(Uri photoUri) {
        Log.d(TAG, "showUserPhoto");
        Glide
                .with(this)
                .load(photoUri)
                .error(R.drawable.ic_users)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imgPhoto);
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
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void showMessage(@StringRes int message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startMainActivity() {
        Log.d(TAG, "startMainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /*
    @Override
    public void onProfileEditImageError(String message) {
        Log.e(TAG, "onProfileEditImageError :" + message);
    }

    @Override
    public void forwardProfileData(Profile profile) {
        Log.d(TAG, "IMAGE SUCCESS");
    }

    @Override
    public void showProfileEditViews() {
        manageIntent(getIntent());
        setToolbar();
        Glide
                .with(this)
                .load(imageUrl)
                .error(R.drawable.ic_users)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageView);
    }

    @Override
    public void disableViews() {
        relativeLayoutButtonImage.setEnabled(false);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void enableViews() {
        relativeLayoutButtonImage.setEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.btn_edit)
    public void editButtonName() {
        Log.d(TAG, "editButtonName");
        Intent intent = new Intent(this, ProfileEditNameActivity.class);
        intent.putExtra("nameUser", name);
        intent.putExtra("phoneNumber", celpho);
        intent.putExtra("photoUrl", imageUrl);

        Log.d(TAG, "editButtonName " + name + "celpho" + celpho);
        startActivity(intent);
    }*/

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1).start(this);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(ProfileEditImagePresenterImpl presenter) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            presenter.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        presenter.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    Uri imageUri;
    private void compressImage(Uri imageUri) {
        ImageCompression imageCompression = new ImageCompression(this.getCacheDir(), this.getContentResolver()) {

            @Override
            protected void onPostExecute(MediaFile mediaFile) {
                Log.d(TAG, "imageCompression path: " + mediaFile.getLocalUri());
                // image here is compressed & ready to be sent to the server
                presenter.editProfileImage(name, celpho, mediaFile.getLocalUri());
            }
        };
        imageCompression.execute(imageUri);// imagePath as a string
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(result.getUri());
                imageUri = result.getUri();
                Log.d(TAG, "ResulURi: " + result.getUri());
                compressImage(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d(TAG, "Cropping failed: " + result.getError());
            }
        }
    }
    */

}
