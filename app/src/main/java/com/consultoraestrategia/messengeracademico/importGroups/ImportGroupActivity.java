package com.consultoraestrategia.messengeracademico.importGroups;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.UseCaseThreadPoolScheduler;
import com.consultoraestrategia.messengeracademico.base.BaseActivity;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportGroupActivity extends BaseActivity<ImportGroupView, ImportGroupPresenter> implements ImportGroupView {

    private static final String TAG = ImportGroupActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bttn_import_groups)
    AppCompatButton bttnImportGroups;
    @BindView(R.id.txt_status)
    AppCompatTextView txtStatus;
    @BindView(R.id.progress)
    ProgressBar progress;


    public static void launch(Context context) {
        Intent intent = new Intent(context, ImportGroupActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected ImportGroupPresenter getPresenter() {
        return new ImportGroupPresenterImpl(
                new UseCaseHandler(new UseCaseThreadPoolScheduler()),
                getResources(),
                GreenRobotEventBus.getInstance(),
                (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE)
        );
    }

    @Override
    protected ImportGroupView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_import_group);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected ViewGroup getRootLayout() {
        return toolbar;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progress;
    }

    @OnClick(R.id.bttn_import_groups)
    public void onViewClicked() {
        presenter.importBttnClicked();
    }

    @Override
    public void enableImportBttn(boolean enabled) {
        bttnImportGroups.setEnabled(enabled);
    }

    @Override
    public void showTxtStatus(String status) {
        txtStatus.setText(status);
    }

    @Override
    public void showDialogToDeleteGroups(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (!TextUtils.isEmpty(title)) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                presenter.onDialogPositiveClicked();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                presenter.onDialogNegativeClicked();
            }
        });
        builder.show();
    }
}
