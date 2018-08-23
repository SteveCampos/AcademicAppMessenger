package com.consultoraestrategia.messengeracademico.profile.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.base.BaseActivity;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenter;
import com.consultoraestrategia.messengeracademico.profile.ProfilePresenterImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kike on 3/06/2017.
 */

public class ProfileActivity extends BaseActivity<ProfileView, ProfilePresenter> implements ProfileView {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.imageProfile)
    ImageView imageCollapsing;
    @BindView(R.id.imageProfileNull)
    ImageView imageCollapsingNull;
    @BindView(R.id.txt_phoneNumber)
    TextView txtPhoneNumber;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txt_status2)
    TextView txtStatus;

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected ProfilePresenter getPresenter() {
        return new ProfilePresenterImpl(
                new UseCaseHandler(new UseCaseThreadPoolScheduler()),
                getResources(),
                GreenRobotEventBus.getInstance()
        );
    }

    @Override
    protected ProfileView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected ViewGroup getRootLayout() {
        return toolbar;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return null;
    }

    @Override
    public void showName(String name) {
        collapsingToolbarLayout.setTitle(name);
    }

    @Override
    public void showPhonenumber(String phonenumber) {
        txtPhoneNumber.setText(phonenumber);
    }

    @Override
    public void showTextVerified(String textVerified) {
        txtStatus.setText(textVerified);
        txtStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_verify, 0, 0, 0);
    }

    @Override
    public void showRegularUserWithoutVerification() {
        txtStatus.setText(R.string.global_user_not_verificated);
    }

    @Override
    public void showPhoto(String url) {
        imageCollapsing.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageCollapsing);
    }

    @Override
    public void showEmptyPhoto() {
        imageCollapsingNull.setVisibility(View.VISIBLE);
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
            case R.id.action_editar:
                presenter.actionEditClicked();
                break;
            case R.id.action_view_contact:
                presenter.actionViewContactClicked();
                break;
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    public void editContactInPhone(String phonenumber) {
        Intent intentActionEdit = new Intent(Intent.ACTION_EDIT);
        intentActionEdit.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(phonenumber)));
        startActivity(intentActionEdit);
    }

    @Override
    public void showContactInPhone(String phonenumber) {
        Intent intentActionInfo = new Intent(Intent.ACTION_VIEW);
        intentActionInfo.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(phonenumber)));
        startActivity(intentActionInfo);
    }

    @Override
    public void launchChatActivity(String phonenumberTo) {
        ChatActivity.startChatActivity(this, phonenumberTo);
    }

    @OnClick(R.id.rll_phonenumber)
    public void onViewClicked() {
        presenter.onPhoneNumberMenuClicked();
    }

}
