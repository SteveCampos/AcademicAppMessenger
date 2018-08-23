package com.consultoraestrategia.messengeracademico.chatList.ui;

import android.content.Context;
import android.os.Bundle;
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
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListListener;
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

public class ChatListFragment extends Fragment implements ChatListView {

    private static final String TAG = ChatListFragment.class.getSimpleName();
    @BindView(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    Unbinder unbinder;

    private ChatListAdapter adapter;
    private ChatListPresenter presenter;

    public ChatListFragment() {
    }

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    private ChatListListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatListListener) {
            this.listener = (ChatListListener) context;
        } else {
            throw new ClassCastException(context.getClass() + " must implement ChatListener!!!");
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
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
        setupRecycler();
        presenter = new ChatListPresenterImpl(this);
        presenter.onCreateView();
    }

    private void setupRecycler() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatListAdapter(new ArrayList<Chat>(), this);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(myRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        myRecyclerView.setAdapter(adapter);
    }

    @Override
    public void addChat(Chat chat) {
        adapter.addChat(chat);
    }

    @Override
    public void updateChat(Chat chat) {
        Log.d(TAG, "updateChat: ");
        adapter.updateChat(chat);
    }

    @Override
    public void removeChat(Chat chat) {
        adapter.removeChat(chat);
    }

    @Override
    public void updateChatList(List<Chat> chats) {
        adapter.setChats(chats);
    }

    @Override
    public void updateChatAndUp(Chat chat) {
        Log.d(TAG, "updateChatAndUp: ");
        adapter.updateAndUp(chat);
    }

    @Override
    public void onChatProfileClick(Contact contact) {
        Bundle args = new Bundle();
        args.putString("imageUri", contact.getPhotoUrl());
        args.putString("nameContact", contact.getName());
        args.putString("phoneNumber", contact.getPhoneNumber());
        DialogProfile newFragment = new DialogProfile();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "TAG");
    }

    @Override
    public void onClick(Chat data, View view) {
        listener.onClick(data, view);
    }

    @Override
    public void onLongClick(Chat data, View view) {
        listener.onLongClick(data, view);
    }
}
