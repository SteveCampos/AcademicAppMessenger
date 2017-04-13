package com.consultoraestrategia.messengeracademico.loadProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity.PREF_STEP_IMPORT_DATA;


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

    private FirebaseContactsHelper firebaseContactsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_profile);
        ButterKnife.bind(this);
        initViews();
        Log.d(TAG, "phone_number: " + getPhoneNumber());
        firebaseContactsHelper = new FirebaseContactsHelper();
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

    @OnClick(R.id.btnGo)
    public void btnGo() {
        final String phoneNumber = getPhoneNumberFromPreferences();
        if (phoneNumber != null) {
            phoneNumberExists(phoneNumber);
        } else {
            showError();
        }
    }

    private void showError() {
        Snackbar.make(activityLoadProfile, "Error", Snackbar.LENGTH_LONG).show();
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

    private void forwardToImportData() {
        Intent intent = new Intent(this, ImportDataActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
