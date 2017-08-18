package com.consultoraestrategia.messengeracademico;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentManager;

import com.consultoraestrategia.messengeracademico.chat.di.ChatComponent;
import com.consultoraestrategia.messengeracademico.chat.di.ChatModule;
import com.consultoraestrategia.messengeracademico.chat.di.DaggerChatComponent;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.main.di.DaggerMainComponent;
import com.consultoraestrategia.messengeracademico.main.di.MainComponent;
import com.consultoraestrategia.messengeracademico.main.di.MainModule;
import com.consultoraestrategia.messengeracademico.notification.FirebaseMessagingView;
import com.consultoraestrategia.messengeracademico.notification.di.DaggerFirebaseMessagingComponent;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingComponent;
import com.consultoraestrategia.messengeracademico.notification.di.FirebaseMessagingModule;
import com.google.firebase.FirebaseApp;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

/**
 * Created by Steve on 16/02/2017.
 */

public class MessengerAcademicoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initEmojis();
        initFirebase();
        initDb();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initEmojis() {
        EmojiManager.install(new EmojiOneProvider()); // This line needs to be executed before any usage of EmojiTextView, EmojiEditText or EmojiButton.
    }

    private void initDb() {
        FlowManager.init(
                new FlowConfig.Builder(this)
                        .openDatabasesOnInit(false).build());
    }

    private void initFirebase() {
        FirebaseApp.initializeApp(this);
    }

    public ChatComponent getChatComponent(Context context, SharedPreferences preferences, ChatMessageListener listener) {
        return DaggerChatComponent
                .builder()
                .chatModule(new ChatModule(listener))
                .messengerAcademicoAppModule(new MessengerAcademicoAppModule(context, preferences))
                .build();
    }


    public MainComponent getMainComponent(Context context, FragmentManager fragmentManager, SharedPreferences preferences) {
        return DaggerMainComponent
                .builder()
                .mainModule(new MainModule(fragmentManager))
                .messengerAcademicoAppModule(new MessengerAcademicoAppModule(context, preferences))
                .build();
    }


    public FirebaseMessagingComponent getFirebaseMessagingComponent(FirebaseMessagingView view, Context context, SharedPreferences preferences) {
        return DaggerFirebaseMessagingComponent
                .builder()
                .firebaseMessagingModule(new FirebaseMessagingModule(view))
                .messengerAcademicoAppModule(new MessengerAcademicoAppModule(context, preferences))
                .build();
    }

}
