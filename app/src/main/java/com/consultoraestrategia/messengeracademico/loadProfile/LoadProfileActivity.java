package com.consultoraestrategia.messengeracademico.loadProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class LoadProfileActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile);
        ButterKnife.bind(this);
        initViews();
        Log.d(TAG, "phone_number: " + getPhoneNumber());
    }

    private void initViews() {
        edtName.setText("Steve");
        Glide.with(this).load("https://lh3.googleusercontent.com/-B42xVxBMpx0/AAAAAAAAAAI/AAAAAAAAEc8/aZ66s9K0gR4/s60-p-rw-no/photo.jpg").into(imgProfile);
    }

    private String getPhoneNumber() {
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
    }




}
