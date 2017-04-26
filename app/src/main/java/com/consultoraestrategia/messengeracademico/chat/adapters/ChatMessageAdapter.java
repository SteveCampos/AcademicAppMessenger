package com.consultoraestrategia.messengeracademico.chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;


/**
 * Created by @stevecampos on 9/03/2017.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT_EMISOR = 777;
    private static final int TYPE_TEXT_RECEPTOR = 778;
    private static final String TAG = ChatMessageAdapter.class.getSimpleName();

    private List<ChatMessage> messages;
    private ChatMessageListener listener;
    private Context context;
    private Contact emisor;

    public ChatMessageAdapter(Contact emisor, List<ChatMessage> messages, ChatMessageListener listener, Context context) {
        this.emisor = emisor;
        this.messages = messages;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType");
        ChatMessage message = messages.get(position);
        if (message.getEmisor().equals(emisor)) {
            return TYPE_TEXT_EMISOR;
        } else {
            return TYPE_TEXT_RECEPTOR;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case TYPE_TEXT_EMISOR:
                View v1 = inflater.inflate(R.layout.chat_item_text_emisor, parent, false);
                viewHolder = new MessageTextEmisorHolder(v1);
                break;
            case TYPE_TEXT_RECEPTOR:
                View v2 = inflater.inflate(R.layout.chat_item_text_receptor, parent, false);
                viewHolder = new MessageTextReceptorHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        ChatMessage message = messages.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_TEXT_EMISOR:
                MessageTextEmisorHolder vh1 = (MessageTextEmisorHolder) holder;
                vh1.bind(message, context, listener);
                break;
            case TYPE_TEXT_RECEPTOR:
                MessageTextReceptorHolder vh2 = (MessageTextReceptorHolder) holder;
                vh2.bind(message, context, listener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void onMessageAdded(ChatMessage message) {
        Log.d(TAG, "onMessageAdded");
        if (messages.contains(message)) {
            onMessagedChanged(message);
        } else {
            messages.add(message);
            notifyItemInserted(getItemCount());
        }
    }

    public void onMessagedChanged(ChatMessage message) {
        Log.d(TAG, "onMessagedChanged");
        if (messages.contains(message)) {
            int position = messages.indexOf(message);
            messages.set(position, message);
            notifyItemChanged(position);
        } else {
            onMessageAdded(message);
        }

    }

    public void addMessageList(List<ChatMessage> messages) {
        Log.d(TAG, "addMessageList");
        if (messages != null && !messages.isEmpty()) {
            this.messages = messages;
            notifyDataSetChanged();
        }
    }
}
