package com.consultoraestrategia.messengeracademico;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.consultoraestrategia.messengeracademico.domain.ChatDbHelper;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.util.Date;

import static com.consultoraestrategia.messengeracademico.entities.Chat.STATE_ACTIVE;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    FloatingActionButton fab;

    Button btn;
    EditText edtMessage;
    EditText edtphoneNumber;
    EditText edtReceptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initViews();
    }

    private void initViews() {
        btn = (Button) findViewById(R.id.btn);
        edtMessage = (EditText) findViewById(R.id.edt_message);
        edtphoneNumber = (EditText) findViewById(R.id.edt_phoneNumber);
        edtReceptor = (EditText) findViewById(R.id.edt_phoneNumber_receptor);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMessage.getText().toString();
                String phoneNumber = edtphoneNumber.getText().toString();
                String phoneNumberReceptor = edtReceptor.getText().toString();

                if (!message.isEmpty() && !phoneNumber.isEmpty() && !phoneNumberReceptor.isEmpty()) {

                    long timestamp = new Date().getTime();

                    Contact emisor = new Contact();
                    emisor.setUserKey(phoneNumber);
                    emisor.setPhoneNumber(phoneNumber);
                    Contact receptor = new Contact();
                    receptor.setUserKey(phoneNumberReceptor);
                    receptor.setPhoneNumber(phoneNumberReceptor);


                    Chat chat = new Chat();
                    chat.setChatKey(emisor.getUserKey() + "_" + receptor.getUserKey());
                    chat.setEmisor(emisor);
                    chat.setReceptor(receptor);
                    chat.setTimestamp(timestamp);
                    chat.setStateTimestamp(timestamp);

                    ChatMessage chatMessage = new ChatMessage();
                    //chatMessage.setChat(chat);
                    chatMessage.setEmisor(emisor);
                    chatMessage.setReceptor(receptor);
                    chatMessage.setMessageText(message);
                    chatMessage.setMessageStatus(ChatMessage.STATUS_WRITED);
                    chatMessage.setMessageType(ChatMessage.TYPE_TEXT);
                    chatMessage.setMessageUri(null);
                    chatMessage.setTimestamp(timestamp);

                    chatMessage.setKeyMessage("key-" + timestamp);
                    chatMessage.setStateChatMessage(STATE_ACTIVE);


                    ChatDbHelper.saveMessage(chatMessage,
                            chat,
                            new Transaction.Success() {
                                @Override
                                public void onSuccess(Transaction transaction) {
                                    Log.d(TAG, "ChatDbHelper.saveMessage onSuccess");
                                }
                            }, new Transaction.Error() {
                                @Override
                                public void onError(Transaction transaction, Throwable error) {
                                    Log.d(TAG, "ChatDbHelper.saveMessage error: " + error);

                                }
                            });
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void showSnackbar(String message) {
        Snackbar.make(fab, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


}
