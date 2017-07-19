package com.consultoraestrategia.messengeracademico.profileEditImage.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ImageCompression;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.profileEditName.ui.ProfileEditNameActivity;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenter;
import com.consultoraestrategia.messengeracademico.profileEditImage.ProfileEditImagePresenterImpl;
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
    CircleImageView imageView;
    @BindView(R.id.txt_nameUser)
    TextView nameUser;
    @BindView(R.id.txt_phoneNumber)
    TextView phoneNumber;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ProfileEditImagePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_image);
        ButterKnife.bind(this);
        presenter = new ProfileEditImagePresenterImpl(this, this);
        presenter.onCreate();
    }


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
            imageUrl = getContact(celpho).getPhotoUri();
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

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
    public void onProfileEditImageError(String message) {
        Log.e(TAG, "onProfileEditImageError :"+message);
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
                .fitCenter()
                .into(imageView);
    }


    @OnClick(R.id.btn_edit)
    public void editButtonName() {
        Log.d(TAG, "editButtonName");
        Intent intent = new Intent(this, ProfileEditNameActivity.class);
        intent.putExtra("nameUser", name);
        intent.putExtra("phoneNumber", celpho);
        intent.putExtra("photoUri", imageUrl);

        Log.d(TAG, "editButtonName " + name + "celpho" + celpho);
        startActivity(intent);
    }

    @OnClick(R.id.relative)
    public void onClickImage() {
        startCropImageActivity(null);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1).start(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    Uri imageUri;

    private void compressImage(Uri imageUri){
        ImageCompression imageCompression = new ImageCompression(this.getCacheDir(), this.getContentResolver()) {
            @Override
            protected void onPostExecute(Uri compressedUri) {
                Log.d(TAG, "imageCompression path: " + compressedUri);
                // image here is compressed & ready to be sent to the server
                presenter.editProfileImage(name, celpho, compressedUri);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }
}
