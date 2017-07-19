package com.consultoraestrategia.messengeracademico.chatList.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chatList.holder.ChatItemHolder;
import com.consultoraestrategia.messengeracademico.chatList.listener.ChatListener;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.Collections;
import java.util.List;

/**
 * Created by jairc on 22/03/2017.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_GROUP_CHAT = 110;
    public static final int VIEW_TYPE_CHAT = 111;

    private Context context;
    private List<Chat> chats;
    private ChatListener listener;
    private Contact me;

    public ChatListAdapter(Context context, Contact contact, List<Chat> chats, ChatListener listener) {
        this.me = contact;
        this.context = context;
        this.chats = chats;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_CHAT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_CHAT:
                View v1 = inflater.inflate(R.layout.item_chat, parent, false);
                viewHolder = new ChatItemHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CHAT:
                ChatItemHolder chatItemHolder = (ChatItemHolder) holder;
                chatItemHolder.bind(me, chat, context, listener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void onChatAdded(Chat chat) {
        if (chats.contains(chat)) {
            onChatChanged(chat);
        } else {
            chats.add(chat);
            notifyItemInserted(0);
            notifyItemRangeChanged(0, chats.size());
        }
    }

    public void onChatChanged(Chat chat) {
        if (chats.contains(chat)) {
            int position = chats.indexOf(chat);
            chats.set(position, chat);
            notifyItemChanged(position);

            /*Collections.swap(this.chats, position, 0);
            notifyItemMoved(position, 0);*/

            //swapItems(position, 0);

        } else {
            onChatAdded(chat);
        }
    }

    // Swap itemA with itemB
    public void swapItems(int itemAIndex, int itemBIndex) {
        //make sure to check if dataset is null and if itemA and itemB are valid indexes.
        Chat itemA = chats.get(itemAIndex);
        Chat itemB = chats.get(itemBIndex);
        chats.set(itemAIndex, itemB);
        chats.set(itemBIndex, itemA);

        notifyDataSetChanged(); //This will trigger onBindViewHolder method from the adapter.
    }


    public void onChatDeleted(Chat chat) {
        if (chats.contains(chat)) {
            int position = chats.indexOf(chat);
            if (chats.remove(chat)) {
                notifyItemRemoved(position);
            }
        }
    }

    public void setChats(List<Chat> chats) {
        if (chats != null && !chats.isEmpty()) {
            this.chats.clear();
            this.chats.addAll(chats);
            notifyDataSetChanged();
        }
    }
}
