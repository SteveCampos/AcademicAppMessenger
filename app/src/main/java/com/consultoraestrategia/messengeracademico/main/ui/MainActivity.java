package com.consultoraestrategia.messengeracademico.main.ui;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.actionMode.BaseActivityActionMode;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListFragment;
import com.consultoraestrategia.messengeracademico.contactList.ui.ContactListFragment;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.GlobalSettings;
import com.consultoraestrategia.messengeracademico.entities.NotificationInbox;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.importGroups.ImportGroupActivity;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.main.di.MainComponent;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingPresenter;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingView;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingComponent;
import com.consultoraestrategia.messengeracademico.profileEditImage.ui.ProfileEditImageActivity;
import com.consultoraestrategia.messengeracademico.utils.JobServiceUtils;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity.EXTRA_RECEPTOR_PHONENUMBER;
import static com.consultoraestrategia.messengeracademico.notification.MyFirebaseMessagingService.MY_NOTIFICATION_ID;


public class MainActivity extends BaseActivityActionMode<Chat, MainView, MainPresenter> implements MainView, FirebaseMessagingView {
    public static final String PREF_STEP = "PREF_STEP";
    public static final String PREF_STEP_COMPLETED = "PREF_STEP_COMPLETED";
    private static final String TAG = MainActivity.class.getSimpleName();

    MyFragmentAdapter fragmentAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;

    private int currentItem = 0;

    private static final String USER_TOPIC = "user_";
    private static final String PREF_USERKEY = "PREF_USERKEY";


    //BEGIN OVERRIDE PROTECTED METHODS FROM ABSTRACT BASE CLASS
    @Override
    protected String getTag() {
        return MainActivity.class.getSimpleName();
    }

    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected MainPresenter getPresenter() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        MainComponent mainComponent = app.getMainComponent(
                getActivity(),
                getSupportFragmentManager(),
                PreferenceManager.getDefaultSharedPreferences(getActivity())
        );
        if (fragmentAdapter == null) {
            mainComponent.getAdapter();
        }
        return mainComponent.getPresenter();
    }

    @Override
    protected MainView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected ViewGroup getRootLayout() {
        return toolbar;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return null;
    }

    //END OVERRIDES FROM ABSTRACT

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isStepsCompleted = isProcessCompleted();
        if (!isStepsCompleted) {
            forwardToStep();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            setupNotificationInjection();
            setupViewPager();
        } else {
            startLoadProfile();
        }
    }



    private void startLoadProfile() {
        forwardToClass(LoadProfileActivity.class);
    }


    private FirebaseMessagingPresenter notificationPresenter;

    private void setupNotificationInjection() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        FirebaseMessagingComponent component = app.getFirebaseMessagingComponent(
                this,
                getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        notificationPresenter = component.getPresenter();
    }

    private void setupViewPager() {
        Log.d(TAG, "setupViewPager");
        if (fragmentAdapter == null) {
            fragmentAdapter = getFragmentAdapter();
        }
        viewpager.setOffscreenPageLimit(2);
        viewpager.setAdapter(fragmentAdapter);
        viewpager.setCurrentItem(currentItem);
        tabs.setupWithViewPager(viewpager);
    }

    private MyFragmentAdapter getFragmentAdapter() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        MainComponent mainComponent = app.getMainComponent(
                getActivity(),
                getSupportFragmentManager(),
                PreferenceManager.getDefaultSharedPreferences(getActivity())
        );
        return mainComponent.getAdapter();
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
                presenter.onActionImportClicked();
                break;
            case R.id.action_profile:
                startActivity(new Intent(this, ProfileEditImageActivity.class));
                break;
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_import_crme_gruops:
                ImportGroupActivity.launch(this);
                break;
            case R.id.action_servers:
                presenter.onServerMenuClicked();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        //CrmeUser
                        //Chat
                        //OfficialMessage
                        //ChatMessage
                        //Contact
                        forwardToClass(LoadProfileActivity.class);
                    }
                });
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
        if (notificationPresenter != null) {
            notificationPresenter.onMessageReceived(message);
        }
    }

    @Override
    public boolean checkGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int status = googleAPI.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        if (googleAPI.isUserResolvableError(status)) {
            final Dialog errorDialog = googleAPI.getErrorDialog(this, status, 1);
            if (errorDialog != null) {
                inflateDialog();
            }
        }
        return false;
    }

    @Override
    public void startImportDataJobService() {
        JobServiceUtils.scheduleImportDataJobService(this);
    }

    @Override
    public void showToolbarProgress() {
        toolbarProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbarProgress() {
        toolbarProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void reloadContacts() {
        ContactListFragment contactListFragment = getContactListFragment();
        if (contactListFragment != null) {
            contactListFragment.reloadContacts();
        }
    }

    public ChatListFragment getChatListFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment :
                fragments) {
            if (fragment instanceof ChatListFragment) {
                return (ChatListFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public void updateChat(Chat chat) {
        ChatListFragment chatListFragment = getChatListFragment();
        if (chatListFragment != null) {
            chatListFragment.updateChat(chat);
        }
    }

    @Override
    public void removeChat(Chat chat) {
        ChatListFragment chatListFragment = getChatListFragment();
        if (chatListFragment != null) {
            chatListFragment.removeChat(chat);
        }
    }

    @Override
    public void showListSingleChooser(String dialogTitle, final List<GlobalSettings> serverList, int positionSelected) {

        if (serverList.isEmpty()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme);
        int size = serverList.size();
        final CharSequence[] singleItems = new CharSequence[size];

        for (int i = 0; i < size; i++) {
            singleItems[i] = serverList.get(i).getNombre();
        }

        if (positionSelected >= serverList.size()) {
            positionSelected = -1;
        }

        builder.setTitle(dialogTitle)
                .setSingleChoiceItems(singleItems, positionSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(getTag(), "setSingleChoiceItems onClick i: " + which);
                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(getTag(), "setPositiveButton onClick i: " + which);
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        if (selectedPosition != -1) {
                            GlobalSettings objectSelected = serverList.get(selectedPosition);
                            presenter.onSelectedServidor(objectSelected, selectedPosition);
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(getTag(), "setNegativeButton onClick i: " + which);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public void showEdtDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_edt, null);
        final EditText editText = dialogView.findViewById(R.id.edt_content);
        builder
                .setView(dialogView)
                .setTitle(R.string.login_dialog_add_server)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (presenter != null) {
                            presenter.onCustomUrlOk(editText.getText().toString());
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public ContactListFragment getContactListFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f :
                fragments) {
            if (f instanceof ContactListFragment) {
                return (ContactListFragment) f;
            }
        }
        return null;
    }

    private void inflateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.google_play_services_title));
        builder.setMessage(getString(R.string.google_play_services_message));
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.marker_google_services))));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.playstore_google_services))));
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

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                presenter.onActionDeleteClicked();
                break;
        }
        return false;
    }

    @Override
    public void onChatProfileClick(Contact contact) {

    }
}
