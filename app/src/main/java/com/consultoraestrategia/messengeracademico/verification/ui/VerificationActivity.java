package com.consultoraestrategia.messengeracademico.verification.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.consultoraestrategia.messengeracademico.main.ui.MainActivity;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.entities.PhoneNumberVerified;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.verification.VerificationPresenter;
import com.consultoraestrategia.messengeracademico.verification.VerificationPresenterImpl;
import com.consultoraestrategia.messengeracademico.verification.sms.SmsListener;
import com.consultoraestrategia.messengeracademico.verification.sms.SmsReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.lamudi.phonefield.PhoneInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity.PREF_STEP_LOAD_PROFILE;

@RuntimePermissions
public class VerificationActivity extends AppCompatActivity implements VerificationView, SmsListener {

    private static final String TAG = VerificationActivity.class.getSimpleName();
    public static final String EXTRA_PHONENUMBER = "EXTRA_PHONENUMBER";
    public static final String PREF_PHONENUMBER = "PREF_PHONENUMBER";
    public static final String PREF_STEP_VERIFICATION = "PREF_STEP_VERIFICATION";
    @BindView(R.id.txt_title)
    AppCompatTextView txttitle;
    @BindView(R.id.txt_subtitle)
    AppCompatTextView txtsubtitle;
    @BindView(R.id.phone_input_layout)
    PhoneInputLayout phoneInputLayout;
    @BindView(R.id.layout_content)
    LinearLayoutCompat layoutcontent;
    @BindView(R.id.layout_head)
    LinearLayoutCompat layoutHead;
    @BindView(R.id.btn_next)
    AppCompatButton btnNext;
    @BindView(R.id.txt_warning)
    AppCompatTextView txtWarning;
    @BindView(R.id.container)
    RelativeLayout container;

    @BindView(R.id.til_code)
    TextInputLayout tilcode;
    @BindView(R.id.btnSendSms)
    AppCompatButton btnSendSms;
    @BindView(R.id.btnRetro)
    AppCompatButton btnRetro;
    @BindView(R.id.edtCode)
    EditText edtCode;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private VerificationPresenter presenter;
    private String phoneNumber;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        VerificationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        presenter = new VerificationPresenterImpl(this);
        presenter.onCreate();
    }

    private void initSmsListener() {
        SmsReceiver.bindListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @OnClick(R.id.btn_next)
    public void onClick() {
        presenter.validatePhoneNumber(phoneInputLayout);
    }

    private void showSnackbar(CharSequence message) {
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void showVerificationViews() {
        phoneInputLayout.setDefaultCountry(getCountryCode());
        txttitle.setText(R.string.verification_title);
        txtsubtitle.setText(R.string.verification_message_smssend);
        btnNext.setVisibility(View.VISIBLE);
        txtWarning.setVisibility(View.VISIBLE);
        phoneInputLayout.setVisibility(View.VISIBLE);
        requestFocus(phoneInputLayout);
    }

    private String getCountryCode() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimCountryIso();
    }

    @Override
    public void hideVerificationViews() {
        phoneInputLayout.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        txtWarning.setVisibility(View.GONE);
    }

    @Override
    public void showDialog(String phoneNumber) {
        confirmPhoneNumber(phoneNumber).show();
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    private AlertDialog confirmPhoneNumber(final String phoneNumber) {
        String message = String.format(getString(R.string.verification_dialog_message_confirm), phoneNumber);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                verificate();
                            }
                        })
                .setNegativeButton("EDITAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return builder.create();
    }

    public void verificate() {
        VerificationActivityPermissionsDispatcher.verificatePhoneNumberWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS})
    public void verificatePhoneNumber() {
        Log.d(TAG, "verificatePhoneNumber");
        presenter.verificatePhoneNumber(phoneNumber);
    }

    @Override
    public void showWatingViews(String phoneNumber) {
        String title = String.format(getString(R.string.verification_title_verificating), phoneNumber);
        String subtitle = String.format(getString(R.string.verification_message_verificating), phoneNumber);
        txttitle.setText(title);
        txtsubtitle.setText(getSpanned(subtitle));
        tilcode.setVisibility(View.VISIBLE);
        btnSendSms.setVisibility(View.VISIBLE);
        btnRetro.setVisibility(View.VISIBLE);
        requestFocus(edtCode);
    }

    private Spanned getSpanned(String htmlText) {
        Log.d(TAG, "getSpanned: " + htmlText);
        Spanned textFormatted;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textFormatted = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            textFormatted = Html.fromHtml(htmlText);
        }
        return textFormatted;
    }

    private void requestFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void hideWatingViews() {
        tilcode.setVisibility(View.GONE);
        btnSendSms.setVisibility(View.GONE);
        btnRetro.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void setCode(String code) {
        edtCode.setText(code);
    }

    @Override
    public void listenMessages() {
        initSmsListener();
    }

    @Override
    public void setTextCodeChangeListener() {
        Log.d(TAG, "setTextCodeChangeListener");
        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged count: " + count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged count: " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged count: " + s.length());
                if (s.length() == 6) {
                    presenter.validateCode(phoneNumber, s.toString());
                }
            }
        });
    }

    private void hideSoftInput() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPhoneNumberVerificated(PhoneNumberVerified numberVerified) {
        hideSoftInput();
        savePreferences(numberVerified.getPhoneNumber());
        forwardToLoadProfile(numberVerified.getPhoneNumber());
    }

    @Override
    public void OnPhoneNumberSendedError(String error) {
        phoneInputLayout.setPhoneNumber(null);
        showSnackbar(error);
    }

    @Override
    public void onPhoneNumberFailedToVerificated(String error) {
        showSnackbar(error);
    }

    @Override
    public void onInvalidCode() {
        showSnackbar(getString(R.string.verification_error_code_invalid));
    }


    @Override
    public void onInvalidPhoneNumber() {
        showSnackbar(getString(R.string.verification_error_invalid_phonenumber));
    }

    @Override
    public void onMessageReceived(String sender, String messageText) {
        Log.d(TAG, "sender: " + sender + ", messageText: " + messageText);
        presenter.onMessageReceived(phoneNumber, messageText);
    }

    @Override
    public boolean checkGooglePlayServicesAvailable() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if (errorDialog != null) {
                inflateDialog();
            }

        }
        return false;
    }

    private void forwardToLoadProfile(String phoneNumber) {
        Intent intent = new Intent(this, LoadProfileActivity.class);
        intent.putExtra(EXTRA_PHONENUMBER, phoneNumber);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void savePreferences(String phoneNumber) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_PHONENUMBER, phoneNumber);
        editor.putString(MainActivity.PREF_STEP, PREF_STEP_LOAD_PROFILE);
        editor.apply();
    }

    @OnClick(R.id.btnRetro)
    public void btnRetro() {
        presenter.initVerificationViews();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    private void inflateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Google Play Services");
        builder.setMessage("Necesita actualizar los paquetes de Google Play Services para continuar");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms")));
                }
                dialog.dismiss();
                checkGooglePlayServicesAvailable();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        AlertDialog alert11 = builder.create();
        alert11.setCancelable(false);
        alert11.show();
    }
}
