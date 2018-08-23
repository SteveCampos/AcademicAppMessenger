package com.consultoraestrategia.messengeracademico.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.edittext.EdittextDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;


public abstract class BaseActivity<V extends BaseView<P>, P extends BasePresenter<V>> extends AppCompatActivity implements BaseView<P> {

    protected abstract String getTag();

    protected abstract AppCompatActivity getActivity();

    protected abstract P getPresenter();

    protected abstract V getBaseView();

    protected abstract Bundle getExtrasInf();

    @Inject
    protected P presenter;

    protected abstract void setContentView();

    protected abstract ViewGroup getRootLayout();

    protected abstract ProgressBar getProgressBar();

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getTag(), "onCreate");
        setContentView();
        setupProgressBar();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            setupPresenter();
        if (presenter != null) presenter.onCreate();
    }

    private void setupProgressBar() {
        progressBar = getProgressBar();
    }


    private void setupPresenter() {
        presenter = getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            presenter = getPresenter();
        }
        setPresenter(presenter);
    }

    @Nullable
    @Override
    public P getLastCustomNonConfigurationInstance() {
        return presenter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(getTag(), "onStart");
        if (presenter != null) presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(getTag(), "onResume");
        if (presenter != null) presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getTag(), "onPause");
        if (presenter != null) presenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(getTag(), "onStop");
        if (presenter != null) presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getTag(), "onDestroy");
        if (presenter != null) presenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(getTag(), "onBackPressed");
        if (presenter != null) presenter.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void setPresenter(P presenter) {
        if (presenter != null) {
            presenter.attachView(getBaseView());
            presenter.setExtras(getExtrasInf());
        }
    }

    @Override
    public P onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    public void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void showMessage(@StringRes int res) {
        Snackbar.make(getRootLayout(), res, Snackbar.LENGTH_LONG).show();
    }

    public void showMessage(CharSequence message) {
        Snackbar.make(getRootLayout(), message, Snackbar.LENGTH_LONG).show();
    }

    public void showImportantMessage(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(R.string.dialog_title_importante_message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showFinalMessage(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(R.string.dialog_title_importante_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        getActivity().finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showEdittextDialog(String text, int inputType, String title, String hint, int requestCode) {
        EdittextDialogFragment fragment = EdittextDialogFragment.newInstance(text, inputType, title, hint, requestCode);
        fragment.show(getSupportFragmentManager(), EdittextDialogFragment.TAG);
    }


    @Override
    public void onTextSubmit(String text, int requestCode) {
        Log.d(getTag(), "onTextSubmit: " + text);
    }

}
