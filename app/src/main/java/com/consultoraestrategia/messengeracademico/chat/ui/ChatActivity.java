package com.consultoraestrategia.messengeracademico.chat.ui;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenterImpl;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.Action;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by @stevecampos on 9/03/2017.
 */


public class ChatActivity extends AppCompatActivity implements ChatView, ChatMessageListener {

    public static final String EXTRA_RECEPTOR_PHONENUMBER = "EXTRA_RECEPTOR_PHONENUMBER";
    private static final String TAG = ChatActivity.class.getSimpleName();
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

    private ChatMessageAdapter adapter;
    private ChatPresenter presenter;
    private Contact emisor;
    private Contact receptor;

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        presenter.onBackPressed();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        presenter = new ChatPresenterImpl(this);
        presenter.onCreate();
        manageIntent(getIntent());
        init();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        presenter.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_atttach_file) {
            Log.d(TAG, "action_atttach_file");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void manageIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_RECEPTOR_PHONENUMBER)) {
            String receptorPhoneNumber = intent.getStringExtra(EXTRA_RECEPTOR_PHONENUMBER);
            presenter.getContactEmisor("");
            presenter.getContactReceptor(receptorPhoneNumber);
        } else {
            finish();
        }
    }


    private void init() {
        edtMessage.addTextChangedListener(watcher);
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
            presenter.afterTextChanged(editable.toString());
        }
    };

    private AppCompatActivity getActivity() {
        return this;
    }

    private void setupRecycler(Contact emisor) {
        adapter = new ChatMessageAdapter(emisor, new ArrayList<ChatMessage>(), this, getActivity());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
    }

    @Override
    public void setReceptor(Contact receptor) {
        this.receptor = receptor;
        txtName.setText(receptor.getName());
        Glide.with(this).load(receptor.getPhotoUri()).into(imgProfile);
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
    public void setEmisor(Contact emisor) {
        if (emisor != null) {
            this.emisor = emisor;
            setupRecycler(emisor);
        }
    }

    @Override
    public void onMessagAdded(ChatMessage message) {
        Log.d(TAG, "onMessagAdded");
        Log.d(TAG, "message getMessageStatus: " + message.getMessageStatus());
        Log.d(TAG, "message getMessageText: " + message.getMessageText());
        adapter.onMessageAdded(message);
        recycler.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onConnectionChanged(Connection connection) {
        Log.d(TAG, "onConnectionChanged: " + connection);
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

    private boolean isGroupChat = false;

    @Override
    public void onUserAction(Contact contact, String action) {
        Log.d(TAG, "onUserAction");
        String actionFormatted = null;
        switch (action) {
            case Action.ACTION_WRITING:
                if (!isGroupChat) {
                    actionFormatted = getString(R.string.global_action_writing);
                } else {
                    actionFormatted = String.format(getString(R.string.global_action_user_writing), contact.getName());
                }
                break;
        }
        if (actionFormatted != null) {
            txtConnection.setVisibility(View.VISIBLE);
            txtConnection.setText(actionFormatted);
        }
    }

    @OnClick(R.id.btn_send)
    @Override
    public void sendMessage() {
        if (edtMessage.getText().toString().isEmpty()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setEmisor(emisor);
        message.setReceptor(receptor);
        message.setMessageText(edtMessage.getText().toString());
        message.setMessageStatus(ChatMessage.STATUS_WRITED);
        message.setMessageType(ChatMessage.TYPE_TEXT);
        message.setMessageUri("");
        message.setTimestamp(new Date().getTime());

        presenter.sendMessage(emisor, receptor, message);
        edtMessage.setText("");
    }

    @Override
    public void onMessageListAdded(List<ChatMessage> messages) {
        Log.d(TAG, "onMessageListAdded");
        if (messages != null) {
            adapter.addMessageList(messages);
            recycler.scrollToPosition(adapter.getItemCount());
        }
    }

    @Override
    public void onMessageReaded(ChatMessage message) {
        Log.d(TAG, "onMessageReaded");
        presenter.setMessageStatusReaded(message);
    }
}
