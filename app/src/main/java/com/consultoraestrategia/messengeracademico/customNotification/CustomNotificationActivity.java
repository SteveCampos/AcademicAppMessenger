package com.consultoraestrategia.messengeracademico.customNotification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kike on 3/06/2017.
 */

//@RuntimePermissions
public class CustomNotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.checkbox_notif_custom)
    AppCompatCheckBox compatCheckBox;

    @BindView(R.id.rel_tono_notification)
    RelativeLayout relativeTonoNotification;
    @BindView(R.id.rel_vibrate_notification)
    RelativeLayout relativeVibrateNotification;
    @BindView(R.id.rel_emergent_notification)
    RelativeLayout relativeEmergentNotification;
    @BindView(R.id.rel_luz_notification)
    RelativeLayout relativeLuzNotification;
    @BindView(R.id.rel_recordatorio_notification)
    RelativeLayout relativeRecordatorioNotification;

    @BindView(R.id.txt_tono_notifications)
    TextView textViewTonoNotifications;
    @BindView(R.id.txt_vibrate_notifications)
    TextView textViewVibrateNotifications;
    @BindView(R.id.txt_emergent_notifications)
    TextView textViewEmergentNotifications;
    @BindView(R.id.txt_luz_notifications)
    TextView textViewLuzNotifications;
    @BindView(R.id.txt_recordatorio_notifications)
    TextView textViewRecordatorioNotifications;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_notification);
        ButterKnife.bind(this);
        setToolbar();
        onClickCheck();
        checkBoxWhite();


    }
    public void setToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notificaciones");
        } else {
            Log.d(getLocalClassName(), "setToolbar null");
        }
    }



    private AppCompatActivity getActivity() {
        return this;
    }


    public void onClickCheck() {

        compatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    relativeTonoNotification.setEnabled(true);
                    relativeVibrateNotification.setEnabled(true);
                    relativeEmergentNotification.setEnabled(true);
                    relativeLuzNotification.setEnabled(true);
                    relativeRecordatorioNotification.setEnabled(true);
                    relativeTonoNotification.setClickable(true);
                    relativeVibrateNotification.setClickable(true);
                    relativeEmergentNotification.setClickable(true);
                    relativeLuzNotification.setClickable(true);
                    relativeRecordatorioNotification.setClickable(true);
                    textViewTonoNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_900));
                    textViewVibrateNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_900));
                    textViewEmergentNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_900));
                    textViewLuzNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_900));
                    textViewRecordatorioNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_900));


                    Log.d(getLocalClassName(), "compatCheckBox.isChecked()==true");
                } else {


                    checkBoxWhite();

                    Log.d(getLocalClassName(), "compatCheckBox.isChecked()==false");
                }
            }
        });
    }


    private void checkBoxWhite() {

        relativeTonoNotification.setClickable(true);
        relativeVibrateNotification.setClickable(true);
        relativeEmergentNotification.setClickable(true);
        relativeLuzNotification.setClickable(true);
        relativeRecordatorioNotification.setClickable(true);

        relativeTonoNotification.setEnabled(false);
        relativeVibrateNotification.setEnabled(false);
        relativeEmergentNotification.setEnabled(false);
        relativeLuzNotification.setEnabled(false);
        relativeRecordatorioNotification.setEnabled(false);


        textViewTonoNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
        textViewVibrateNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
        textViewEmergentNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
        textViewLuzNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
        textViewRecordatorioNotifications.setTextColor(ContextCompat.getColor(getActivity(), R.color.md_grey_300));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_custom_notification, menu);
        return true;
    }
}
