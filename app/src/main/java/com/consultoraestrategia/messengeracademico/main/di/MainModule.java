package com.consultoraestrategia.messengeracademico.main.di;

import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.chatList.ui.ChatListFragment;
import com.consultoraestrategia.messengeracademico.contactList.ui.ContactListFragment;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.groupList.GroupListFragment;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.main.ChatsFragment;
import com.consultoraestrategia.messengeracademico.main.ConnectionInteractor;
import com.consultoraestrategia.messengeracademico.main.MainPresenter;
import com.consultoraestrategia.messengeracademico.main.MainPresenterImpl;
import com.consultoraestrategia.messengeracademico.main.adapters.MyFragmentAdapter;
import com.consultoraestrategia.messengeracademico.main.domain.usecase.ListenForUserMessages;
import com.consultoraestrategia.messengeracademico.storage.DefaultSharedPreferencesHelper;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by @stevecampos on 31/05/2017.
 */

@Module
public class MainModule {

    private static String TAG = MainModule.class.getSimpleName();

    FragmentManager fragmentManager;


    public MainModule(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Provides
    @Singleton
    MyFragmentAdapter provideMyFragmentAdapter(Resources resources, FragmentManager supportFragmentManager, ChatsFragment chatsFragment, ChatListFragment chatListFragment, ContactListFragment contactListFragment, GroupListFragment groupListFragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_principal, chatsFragment);
        fragmentTransaction.commit();


        MyFragmentAdapter adapter = new MyFragmentAdapter(supportFragmentManager);
        // adapter.addFragment(chatsFragment, "CALL");
        adapter.addFragment(chatListFragment, resources.getString(R.string.fragment_chatlist_title));
        adapter.addFragment(contactListFragment, resources.getString(R.string.fragment_contacts_title));
        adapter.addFragment(groupListFragment, resources.getString(R.string.fragment_grouplist_title));

        return adapter;
    }

    @Singleton
    @Provides
    FragmentManager provideSupportFragmentManager() {
        return this.fragmentManager;
    }

    @Provides
    @Singleton
    GroupListFragment provideGroupListFragment() {
        return GroupListFragment.newInstance();
    }

    @Provides
    @Singleton
    ChatsFragment provideChatsFragment() {
        return new ChatsFragment();
    }

    @Provides
    @Singleton
    ChatListFragment provideChatListFragment() {
        return ChatListFragment.newInstance();
    }

    @Provides
    @Singleton
    ContactListFragment provideContactListFragment() {
        return ContactListFragment.newInstance();
    }


    @Provides
    @Singleton
    MainPresenter provideMainPresenter(UseCaseHandler useCaseHandler, ListenForUserMessages listenForMessages, EventBus eventBus, ConnectionInteractor connectionInteractor, Long timestamp, com.google.firebase.auth.FirebaseUser mainUser, Resources res) {
        return new MainPresenterImpl(useCaseHandler, listenForMessages, eventBus, connectionInteractor, timestamp, mainUser, res);
    }

    @Provides
    @Singleton
    Long provideTimeStamp() {
        return new Date().getTime();
    }


    @Provides
    @Singleton
    ListenForUserMessages provideListenForUserMessages(ChatRepository repository) {
        return new ListenForUserMessages(repository);
    }


}
