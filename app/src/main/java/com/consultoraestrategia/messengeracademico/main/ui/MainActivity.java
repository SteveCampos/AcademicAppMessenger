package com.consultoraestrategia.messengeracademico.main.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListFragment;
import com.consultoraestrategia.messengeracademico.contactList.ui.ContactListFragment;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Contact_Table;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.main.ChatsFragment;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.MainPresenterImpl;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.main.di.MainComponent;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity.PREF_PHONENUMBER;

public class MainActivity extends AppCompatActivity implements MainView {
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
        boolean isStepsCompleted = isProcessCompleted();
        if (!isStepsCompleted) {
            forwardToStep();
            return;
        }
        setupInjection();
        setupViewPager();
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }


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
            //presenter.onStop();
        }
        super.onStop();
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
            case VerificationActivity.PREF_STEP_VERIFICATION:
                stepClass = VerificationActivity.class;
                break;
            case LoadProfileActivity.PREF_STEP_LOAD_PROFILE:
                stepClass = LoadProfileActivity.class;
                break;
            case ImportDataActivity.PREF_STEP_IMPORT_DATA:
                stepClass = ImportDataActivity.class;
                break;
            case PREF_STEP_COMPLETED:
                completed = true;
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
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ImportDataActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void setPresenter(MainPresenterImpl presenter) {

    }
}
