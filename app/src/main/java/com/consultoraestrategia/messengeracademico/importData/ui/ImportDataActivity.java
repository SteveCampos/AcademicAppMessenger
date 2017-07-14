package com.consultoraestrategia.messengeracademico.importData.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.importData.DatosGeneralesAsyntask;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.importData.ImportDataPresenterImpl;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ImportDataActivity extends AppCompatActivity implements ImportDataView {


    public static final String PREF_STEP_IMPORT_DATA = "PREF_STEP_IMPORT_DATA";
    public static final String TAG = ImportDataActivity.class.getSimpleName();
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_PHOTO_URI = "EXTRA_PHOTO_URI";
    public static final String EXTRA_PHONENUMBER = "EXTRA_PHONENUMBER";

    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.imgProfile)
    CircleImageView imgProfile;
    @BindView(R.id.txtSubtitle)
    AppCompatTextView txtSubtitle;
    @BindView(R.id.txtContent)
    AppCompatTextView txtContent;
    @BindView(R.id.layoutAnimation)
    RelativeLayout layoutAnimation;
    @BindView(R.id.btnGo)
    AppCompatButton btnGo;
    @BindView(R.id.txtNotice)
    AppCompatTextView txtNotice;
    @BindView(R.id.activity_import_contacts)
    RelativeLayout activityImportContacts;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ImportDataPresenterImpl presenter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        ImportDataActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        ButterKnife.bind(this);
        manageIntent(getIntent());
        presenter = new ImportDataPresenterImpl(this, getActivity());
        presenter.onCreate();
    }

    private void manageIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_NAME) && intent.hasExtra(EXTRA_PHOTO_URI) && intent.hasExtra(EXTRA_PHONENUMBER)) {
            String name = intent.getStringExtra(EXTRA_NAME);
            String uri = intent.getStringExtra(EXTRA_PHOTO_URI);
            String phoneNumber = intent.getStringExtra(EXTRA_PHONENUMBER);

            Contact contact = new Contact();
            contact.setName(name);
            contact.setPhotoUri(uri);
            contact.setPhoneNumber(phoneNumber);

            setProfile(contact);
        } else {
            DefaultSharedPreferencesHelper helper = new DefaultSharedPreferencesHelper(PreferenceManager.getDefaultSharedPreferences(this));

            Contact mainContact = helper.getContact();
            if (mainContact != null) {
                setProfile(mainContact);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }


    @OnClick(R.id.btnGo)
    public void onClick() {
        ImportDataActivityPermissionsDispatcher.importDataWithCheck(this);
    }


    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    public void importData() {
        presenter.handleClick(getPhoneNumberFromPreferences());
        new DatosGeneralesAsyntask().execute(getPhoneNumberFromPreferences());
    }

    private String getPhoneNumberFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, null);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void setProfile(Contact contact) {
        String title = String.format(getString(R.string.importdata_title), contact.getName());
        txtTitle.setText(title);
        Glide
                .with(this)
                .load(contact.getPhotoUri())
                .error(R.drawable.ic_users)
                .into(imgProfile);
    }

    @Override
    public void showWelcomeView() {
        setVisibleWelcomeViews(View.VISIBLE);
    }

    @Override
    public void hideWelcomeView() {
        setVisibleWelcomeViews(View.GONE);
    }

    private void setVisibleWelcomeViews(int visible) {
        txtTitle.setVisibility(visible);
        imgProfile.setVisibility(visible);
        txtSubtitle.setVisibility(visible);
        txtContent.setVisibility(visible);
        btnGo.setVisibility(visible);
    }

    @Override
    public void showImportView() {
        txtTitle.setVisibility(View.VISIBLE);
        txtSubtitle.setVisibility(View.VISIBLE);
        txtContent.setVisibility(View.VISIBLE);
        btnGo.setVisibility(View.VISIBLE);

        txtTitle.setText(R.string.importdata_title_import);
        txtSubtitle.setText(R.string.importdata_subtitle_import);
        txtContent.setText(R.string.importdata_content_import);
        btnGo.setText(R.string.importdata_button_import);
    }

    @Override
    public void hideImportView() {
        txtTitle.setVisibility(View.GONE);
        txtSubtitle.setVisibility(View.GONE);
        txtContent.setVisibility(View.GONE);
        btnGo.setVisibility(View.GONE);
    }

    @Override
    public void onInstitutionsImported(int count) {

    }

    @Override
    public void onContactsImported(final int count) {
        final String message = String.format(getString(R.string.importdata_message_contacts_imported), count);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtNotice.setVisibility(View.VISIBLE);
                txtNotice.setText(message);
            }
        });
    }

    @Override
    public void showProgress() {
        btnGo.setVisibility(View.GONE);
        layoutAnimation.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        btnGo.setVisibility(View.VISIBLE);
        layoutAnimation.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onImportSuccess() {
        btnGo.setText(R.string.importdata_message_ready);
        //txtNotice.setText(R.string.importdata_notice_success);
    }

    @Override
    public void onImportError() {
        btnGo.setText(R.string.importdata_button_go);
        txtNotice.setText(R.string.importdata_notice_error);
    }


    private void saveStep() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MainActivity.PREF_STEP, MainActivity.PREF_STEP_COMPLETED);
        editor.apply();
    }

    @Override
    public void goToMain() {
        saveStep();
        Intent intent = new Intent(this, MainActivity.class);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
