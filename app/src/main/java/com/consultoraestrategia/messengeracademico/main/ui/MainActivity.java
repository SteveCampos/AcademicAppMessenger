package com.consultoraestrategia.messengeracademico.main.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ui.ChatActivity;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListFragment;
import com.consultoraestrategia.messengeracademico.contactList.ui.ContactListFragment;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.loadProfile.ui.LoadProfileActivity;
import com.consultoraestrategia.messengeracademico.main.ChatsFragment;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.MainPresenterImpl;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.verification.ui.VerificationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainView {
    public static final String PREF_STEP = "PREF_STEP";
    public static final String PREF_STEP_COMPLETED = "PREF_STEP_COMPLETED";
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainPresenter presenter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        forwardToStep();
        init();
    }

    private void init() {
        setupViewPager();
        presenter = new MainPresenterImpl(this);
        presenter.onCreate();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        presenter.getPhoneNumber(preferences);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        if (presenter != null) {
            presenter.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    private void setupViewPager() {
        Log.d(TAG, "setupViewPager");
        viewpager.setOffscreenPageLimit(2);
        fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());

        fragmentAdapter.addFragment(new ChatsFragment(), "Calls");
        fragmentAdapter.addFragment(ChatListFragment.newInstance(), getString(R.string.fragment_chatlist_title));
        fragmentAdapter.addFragment(ContactListFragment.newInstance(), getString(R.string.fragment_contacts_title));

        viewpager.setAdapter(fragmentAdapter);
        viewpager.setCurrentItem(currentItem);
        tabs.setupWithViewPager(viewpager);
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
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_RECEPTOR_PHONENUMBER, contact.getPhoneNumber());
        getActivity().startActivity(intent);
    }
}
