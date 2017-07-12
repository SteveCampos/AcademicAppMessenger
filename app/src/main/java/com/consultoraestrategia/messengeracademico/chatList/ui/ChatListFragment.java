package com.consultoraestrategia.messengeracademico.chatList.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chatList.ChatListPresenter;
import com.consultoraestrategia.messengeracademico.chatList.ChatListPresenterImpl;
import com.consultoraestrategia.messengeracademico.chatList.adapter.ChatListAdapter;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListener;
import com.consultoraestrategia.messengeracademico.dialogProfile.DialogProfile;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jairc on 22/03/2017.
 */

public class ChatListFragment extends Fragment implements ChatListener, ChatListView {

    private static final String TAG = ChatListFragment.class.getSimpleName();
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    Unbinder unbinder;

    private DividerItemDecoration mDividerItemDecoration;

    private ChatListAdapter adapter;
    private ChatListPresenter presenter;

    private String phoneNumber;
    private Contact me;
    FragmentManager fm = getFragmentManager();

    public ChatListFragment() {
    }

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setRetainInstance(true);
        init();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        super.onDestroy();
    }

    private void init() {
        presenter = new ChatListPresenterImpl(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        presenter.onCreateView();
        presenter.getPhoneNumber(preferences);
    }

    private void setupRecycler(Contact contact) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatListAdapter(getActivity(), contact, new ArrayList<Chat>(), this);
        mDividerItemDecoration = new DividerItemDecoration(myRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        //myRecyclerView.addItemDecoration(mDividerItemDecoration);
        myRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onChatAdded(Chat chat) {
        adapter.onChatAdded(chat);
    }

    @Override
    public void onChatChanged(Chat chat) {
        adapter.onChatChanged(chat);
    }

    @Override
    public void onChatRemoved(Chat chat) {
        adapter.onChatDeleted(chat);
    }

    @Override
    public void onChatsChanged(List<Chat> chats) {
        adapter.setChats(chats);
    }

    @Override
    public void setContact(Contact contact) {
        if (contact != null) {
            me = contact;
            setupRecycler(me);
            presenter.getChats(me);
        }
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        Log.d(TAG, "setPhoneNumber");
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
            presenter.getContact(phoneNumber);
        }
    }

    @Override
    public void onChatClickedListener(Chat chat) {
        Log.d(TAG, "onChatClickedListener");
        presenter.onChatClicked(chat);
    }

    @Override
    public void onImageClickdListener(Contact contact) {
        /*DialogProfile newFragment = new DialogProfile();
        newFragment.show(getFragmentManager(), "datePicker");
        Toast.makeText(getActivity(),"Contact: "+contact.getPhoneNumber(),Toast.LENGTH_LONG).show();
            */

        Bundle args = new Bundle();
        args.putString("imageUri", contact.getPhotoUri());
        args.putString("nameContact", contact.getName());
        args.putString("phoneNumber",contact.getPhoneNumber());
        DialogProfile newFragment = new DialogProfile();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "TAG");
    }
}
