package com.consultoraestrategia.messengeracademico.chatList.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chatList.holder.ChatItemHolder;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jairc on 22/03/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private static final int VIEW_TYPE_GROUP_CHAT = 110;
    private static final int VIEW_TYPE_CHAT = 111;

    private List<Chat> chats;
    private ChatListListener listener;
    private FirebaseUser mainUser;
    //private Contact me;

    public ChatListAdapter(List<Chat> chats, ChatListListener listener) {
        this.mainUser = FirebaseAuth.getInstance().getCurrentUser();
        this.chats = chats;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CHAT;
    }

    @Override
    public @NonNull
    RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            default:
                View v1 = inflater.inflate(R.layout.item_chat, parent, false);
                viewHolder = new ChatItemHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CHAT:
                ChatItemHolder chatItemHolder = (ChatItemHolder) holder;
                chatItemHolder.bind(mainUser, chat, listener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void addChat(Chat chat) {
        if (chats.contains(chat)) {
            updateChat(chat);
        } else {
            chats.add(chat);
            notifyChatsChanged();
            /*notifyItemInserted(0);
            notifyItemRangeChanged(0, chats.size());*/
        }
    }

    public void removeChat(Chat chat) {
        int position = chats.indexOf(chat);
        if (position == -1) return;
        chats.remove(position);
        notifyItemRemoved(position);
    }

    public void updateChat(Chat chat) {
        if (chats.contains(chat)) {
            int position = chats.indexOf(chat);
            chats.set(position, chat);
            notifyItemChanged(position);
        } else {
            addChat(chat);
        }
    }

    public void updateAndUp(Chat chat) {
        int position = chats.indexOf(chat);
        if (position == -1) {
            addChat(chat);
            return;
        }
        chats.remove(position);
        chats.add(0, chat);
        notifyDataSetChanged();
    }

    public void setChats(List<Chat> chats) {
        if (chats != null && !chats.isEmpty()) {
            this.chats.clear();
            this.chats.addAll(chats);
            notifyChatsChanged();
        }
    }

    private void notifyChatsChanged() {
        Collections.sort(chats, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
            }
        });
        notifyDataSetChanged();
    }
}
