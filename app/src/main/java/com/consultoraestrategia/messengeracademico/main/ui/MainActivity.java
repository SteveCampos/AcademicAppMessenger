package com.consultoraestrategia.messengeracademico.main.ui;

import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.MainPresenterImpl;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.main.di.MainComponent;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageActivity;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingPresenter;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingView;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingComponent;
import com.consultoraestrategia.messengeracademico.prueba.TestActivity;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;
import static com.consultoraestrategia.messengeracademico.notification.MyFirebaseMessagingService.MY_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity implements MainView, FirebaseMessagingView {
    public static final String PREF_STEP = "PREF_STEP";
    public static final String PREF_STEP_COMPLETED = "PREF_STEP_COMPLETED";
    private static final String TAG = MainActivity.class.getSimpleName();

    MainPresenter presenter;
    MyFragmentAdapter fragmentAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private int currentItem = 1;

    private static final String USER_TOPIC = "user_";
    private static final String PREF_USERKEY = "PREF_USERKEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSharedPreferences();
        boolean isStepsCompleted = isProcessCompleted();
        if (!isStepsCompleted) {
            forwardToStep();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            setupInjection();
            setupViewPager();
        } else {
            startLoadProfile();
        }
    }

    private void startLoadProfile() {
        forwardToClass(LoadProfileActivity.class);
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }


    private FirebaseMessagingPresenter notificationPresenter;

    private void setupInjection() {
        Log.d(TAG, "setupInjection");
        presenter = (MainPresenter) getLastCustomNonConfigurationInstance();
        if (presenter == null) {
            Log.d(TAG, "presenter == null, setup injection...");
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            MainComponent mainComponent = app.getMainComponent(getActivity(), getSupportFragmentManager(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
            presenter = mainComponent.getPresenter();
            fragmentAdapter = mainComponent.getAdapter();
        }

        if (fragmentAdapter == null) {
            Log.d(TAG, "adapter == == null, setup injection...");
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            MainComponent mainComponent = app.getMainComponent(getActivity(), getSupportFragmentManager(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
            fragmentAdapter = mainComponent.getAdapter();
        }

        presenter.attachView(this);
        presenter.onCreate();
        setupNotificationInjection();
    }

    private void setupNotificationInjection() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        FirebaseMessagingComponent component = app.getFirebaseMessagingComponent(
                this,
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        notificationPresenter = component.getPresenter();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (presenter != null) {
            presenter.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        if (presenter != null) {
            presenter.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (presenter != null) {
            presenter.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if (presenter != null) {
            presenter.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        if (presenter != null) {
            presenter.onStart();
        }
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (presenter != null) {
            presenter.onDestroy();
        }
        super.onDestroy();
    }

    private void setupViewPager() {
        Log.d(TAG, "setupViewPager");
        viewpager.setOffscreenPageLimit(2);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.setCurrentItem(currentItem);
        tabs.setupWithViewPager(viewpager);
    }

    public boolean isProcessCompleted() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String step = preferences.getString(PREF_STEP, VerificationActivity.PREF_STEP_VERIFICATION);
        return step.equals(PREF_STEP_COMPLETED);
    }

    public void forwardToStep() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String step = preferences.getString(PREF_STEP, VerificationActivity.PREF_STEP_VERIFICATION);
        Class stepClass = null;
        boolean completed = false;
        switch (step) {
            /*
            case VerificationActivity.PREF_STEP_VERIFICATION:
                stepClass = VerificationActivity.class;
                break;*/
            case LoadProfileActivity.PREF_STEP_LOAD_PROFILE:
                stepClass = LoadProfileActivity.class;
                break;
            case ImportDataActivity.PREF_STEP_IMPORT_DATA:
                stepClass = ImportDataActivity.class;
                break;
            case PREF_STEP_COMPLETED:
                completed = true;
                break;
            default:
                stepClass = LoadProfileActivity.class;
                break;
        }

        if (!completed) {
            forwardToClass(stepClass);
        }
    }


    public void forwardToClass(Class clase) {
        finish();
        Intent intent = new Intent(this, clase);
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            default:
                break;
            case R.id.action_import:
                startActivity(new Intent(this, ImportDataActivity.class));
                break;
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileEditImageActivity.class));
                break;
            case R.id.action_logout:
                logout();
                break;
            /*case R.id.action_test:
                startActivity(new Intent(this, TestActivity.class));
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        forwardToClass(LoadProfileActivity.class);
                    }
                });
    }

    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void startChat(Contact contact) {
        Log.d(TAG, "startChat");
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_RECEPTOR_PHONENUMBER, contact.getPhoneNumber());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void fireNotification(ChatMessage message) {
        Log.d(TAG, "fireNotification");
        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception ex) {
            Log.e(TAG, "ex: " + ex);
            //e.printStackTrace();
        }*/

        if (notificationPresenter != null) {
            notificationPresenter.onMessageReceived(message);
        }
    }

    @Override
    public void setPresenter(MainPresenterImpl presenter) {

    }

    String phoneNumber;

    private void getSharedPreferences() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        phoneNumber = settings.getString(MainActivity.PREF_STEP, "");

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        phoneNumber = sharedPref.getString(VerificationActivity.PREF_PHONENUMBER, "+51993061806");
        Log.d(TAG, " phoneNumber : " + phoneNumber);
    }


    private String getPhoneNumberFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(VerificationActivity.PREF_PHONENUMBER, "+51993061806");
    }

    @Override
    public void createNotification(NotificationInbox notification) {
        Log.d(TAG, "createNotification");

        String action = notification.getAction();
        String bigContentTitle = notification.getBigContentTitle();
        String summaryText = notification.getSummaryText();
        String largeIconUri = notification.getLargeIconUri();
        int smallIcon = notification.getSmallIcon();
        Bitmap largeIcon = notification.getLargeIcon();
        List<String> lines = notification.getLines();

        Log.d(TAG, "bigContentTitle: " + bigContentTitle);
        Log.d(TAG, "largeIconUri: " + largeIconUri);
        Log.d(TAG, "action: " + action);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(bigContentTitle);
        builder.setContentText(summaryText);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();


        if (bigContentTitle != null) {
            inboxStyle.setBigContentTitle(bigContentTitle);
        }
        if (summaryText != null) {
            inboxStyle.setSummaryText(summaryText);
        }
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                inboxStyle.addLine(line);
            }
        }

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(EXTRA_RECEPTOR_PHONENUMBER, action);

        intent.setAction(action);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (smallIcon > 0) {
            builder.setSmallIcon(smallIcon);
        }

        if (largeIcon != null) {
            builder.setLargeIcon(largeIcon);
        }

        builder.setSound(defaultSoundUri);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        builder.setStyle(inboxStyle);

        NotificationManagerCompat.from(this).notify(MY_NOTIFICATION_ID, builder.build());
    }
}
