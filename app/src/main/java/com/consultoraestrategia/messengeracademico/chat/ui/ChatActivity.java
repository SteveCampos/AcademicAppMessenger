package com.consultoraestrategia.messengeracademico.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.di.ChatComponent;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by @stevecampos on 9/03/2017.
 */


public class ChatActivity extends AppCompatActivity implements ChatView, ChatMessageListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_RECEPTOR_PHONENUMBER = "EXTRA_RECEPTOR_PHONENUMBER";

    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_connection)
    TextView txtConnection;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.btn_emoji)
    ImageView btnEmoji;
    @BindView(R.id.edt_message)
    AppCompatEditText edtMessage;
    @BindView(R.id.input_layout_message)
    TextInputLayout inputLayoutMessage;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.btn_send)
    FloatingActionButton btnSend;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Inject
    ChatMessageAdapter adapter;
    @Inject
    ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        edtMessage.addTextChangedListener(watcher);
        setupInjection();
        setupRecycler();
        initPresenter();
    }

    private void initPresenter() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_RECEPTOR_PHONENUMBER)) {
            String receptorPhoneNumber = intent.getStringExtra(EXTRA_RECEPTOR_PHONENUMBER);
            presenter.onCreate();
            presenter.loadReceptor(receptorPhoneNumber);
        } else {
            finish();
        }
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
    protected void onStart() {
        Log.d(TAG, "onStart");
        presenter.onStart();
        super.onStart();
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        presenter.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    private void setupInjection() {
        Log.d(TAG, "setupInjection");
        presenter = (ChatPresenter) getLastCustomNonConfigurationInstance();

        if (presenter == null) {
            Log.d(TAG, "presenter != null");
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this);
            presenter = chatComponent.getPresenter();
            adapter = chatComponent.getAdapter();
        }
        if (adapter == null) {
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this);
            adapter = chatComponent.getAdapter();
        }

        presenter.attachView(this);
    }

    private void setupRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        adapter.setRecyclerView(recycler);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void setPresenter(ChatPresenter presenter) {
        this.presenter = presenter;
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
    public void showReceptor(Contact receptor) {
        txtName.setText(receptor.getName());
        Glide.with(this).load(receptor.getPhotoUri()).into(imgProfile);
    }

    @Override
    public void addMessages(List<ChatMessage> messages) {
        Log.d(TAG, "addMessages");
        if (messages != null) {
            adapter.addMessageList(messages);
        }
    }

    @Override
    public void addMessage(ChatMessage message) {
        Log.d(TAG, "addMessage");
        if (message != null) {
            adapter.onMessageAdded(message);
        }
    }

    @Override
    public void onMessageReaded(ChatMessage message) {
        Log.d(TAG, "onMessageReaded");
        presenter.readMessage(message);
    }


    @OnClick(R.id.btn_send)
    @Override
    public void sendMessageText() {
        String text = edtMessage.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        edtMessage.setText("");
        presenter.sendMessageText(text);
    }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    public void showConnection(Connection connection) {
        Log.d(TAG, "showConnection: " + connection);
        if (connection == null) {
            txtConnection.setVisibility(View.GONE);
            return;
        }

        boolean online = connection.isOnline();
        long lastConnection = connection.getTimestamp();

        String connectionText;
        if (online) {
            connectionText = getString(R.string.global_notice_online);
        } else {
            connectionText = String.format(getString(R.string.chat_info_connection_lastseen), SIMPLE_DATE_FORMAT.format(lastConnection));
        }
        txtConnection.setVisibility(View.VISIBLE);
        txtConnection.setText(connectionText);
    }

    private boolean isWriting;
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.d(TAG, "afterTextChanged editable.length(): " + editable.length());
            presenter.changeWritingState(editable.toString());
        }
    };

    @Override
    public void showReceptorAction(Contact contact, String action) {
        Log.d(TAG, "showReceptorAction");
        String actionFormatted = getString(R.string.global_action_writing);
        txtConnection.setVisibility(View.VISIBLE);
        txtConnection.setText(actionFormatted);
    }
}
