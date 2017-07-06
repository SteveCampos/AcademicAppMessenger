package com.consultoraestrategia.messengeracademico.profile.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.customNotification.CustomNotificationActivity;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenter;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.imageProfile)
    ImageView imageCollapsing;
    @BindView(R.id.imageProfileNull)
    ImageView imageCollapsingNull;
    @BindView(R.id.txt_phoneNumber)
    TextView phoneNumber;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String celpho;
    private ProfilePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        manageIntent(getIntent());
        presenter = new ProfilePresenterImpl(this, getContentResolver());
        presenter.onCreate();

    }


    private void manageIntent(Intent intent) {
        if (intent.hasExtra(ImportDataActivity.EXTRA_PHOTO_URI) && intent.hasExtra(ImportDataActivity.EXTRA_NAME) && intent.hasExtra(ImportDataActivity.EXTRA_PHONENUMBER)) {
            String imageUrl = intent.getStringExtra(ImportDataActivity.EXTRA_PHOTO_URI);
            String nameUser = intent.getStringExtra(ImportDataActivity.EXTRA_NAME);
            celpho = intent.getStringExtra(ImportDataActivity.EXTRA_PHONENUMBER);

            loadContact(imageUrl, nameUser, celpho);
        } else {
            onProfileError("manageIntent null");
        }
    }

    private void loadContact(String imageUrl, String nameUser, String celpho) {
        validateImage(imageUrl);
        phoneNumber.setText(celpho);
        collapsingToolbarLayout.setTitle(nameUser);
    }

    private void validateImage(String imageUrl) {
        if (imageUrl != null) {
            imageCollapsing.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageCollapsing);
        }else {
            imageCollapsingNull.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        Intent i = new Intent(this, ChatActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(i);
        finish();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_editar:
                presenter.verificatedProfileEdit(celpho);
                return true;
            case R.id.menu_contactos:
                presenter.verificatedProfileInformation(celpho);
                return true;
            default:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void forwardToEditData(long idphone) {
        Intent intentActionEdit = new Intent(Intent.ACTION_EDIT);
        intentActionEdit.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idphone));
        startActivity(intentActionEdit);
    }

    @Override
    public void forwardToShowData(long idphone) {
        Intent intentActionInfo = new Intent(Intent.ACTION_VIEW);
        intentActionInfo.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idphone));
        startActivity(intentActionInfo);
    }

    @Override
    public void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            onProfileError("setToolbar null");
        }
    }

    @Override
    public void onProfileError(String error) {
        showSnackbar(error);
    }

    private void showSnackbar(CharSequence message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show();
    }


    @OnClick(R.id.rel_child_second)
    public void relativeNotifications() {
        Intent intent = new Intent(this, CustomNotificationActivity.class);
        startActivity(intent);
    }

}
