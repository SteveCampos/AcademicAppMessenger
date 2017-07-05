package com.consultoraestrategia.messengeracademico.chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageImageEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageImageReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMISOR_TEXT = 100;
    private static final int TYPE_EMISOR_IMAGE = 101;

    private static final int TYPE_RECEPTOR_TEXT = 200;
    private static final int TYPE_RECEPTOR_IMAGE = 201;


    private static final String TAG = ChatMessageAdapter.class.getSimpleName();

    private List<ChatMessage> messages;
    private ChatMessageListener listener;
    private Context context;
    private Contact emisor;
    private RecyclerView recyclerView;

    public ChatMessageAdapter(Contact emisor, List<ChatMessage> messages, ChatMessageListener listener, Context context) {
        Log.d(TAG, "ChatMessageAdapter new Instance: " + this.hashCode());
        this.emisor = emisor;
        this.messages = messages;
        this.listener = listener;
        this.context = context;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT)) {
            if (message.getEmisor().equals(emisor)) {
                return TYPE_EMISOR_TEXT;
            } else {
                return TYPE_RECEPTOR_TEXT;
            }
        }
        if (message.getMessageType().equals(ChatMessage.TYPE_IMAGE)) {
            if (message.getEmisor().equals(emisor)) {
                return TYPE_EMISOR_IMAGE;
            } else {
                return TYPE_RECEPTOR_IMAGE;
            }
        }

        return TYPE_EMISOR_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case TYPE_EMISOR_TEXT:
                View v1 = inflater.inflate(R.layout.chat_item_text_emisor, parent, false);
                viewHolder = new MessageTextEmisorHolder(v1);
                break;
            case TYPE_RECEPTOR_TEXT:
                View v2 = inflater.inflate(R.layout.chat_item_text_receptor, parent, false);
                viewHolder = new MessageTextReceptorHolder(v2);
                break;
            case TYPE_EMISOR_IMAGE:
                View v3 = inflater.inflate(R.layout.chat_item_image_emisor, parent, false);
                viewHolder = new MessageImageEmisorHolder(v3);
                break;
            case TYPE_RECEPTOR_IMAGE:
                View v4 = inflater.inflate(R.layout.chat_item_image_receptor, parent, false);
                viewHolder = new MessageImageReceptorHolder(v4);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_EMISOR_TEXT:
                MessageTextEmisorHolder vh1 = (MessageTextEmisorHolder) holder;
                vh1.bind(message, MessageTextEmisorHolder.getDrawableFromMessageStatus(message.getMessageStatus(), context));
                break;
            case TYPE_RECEPTOR_TEXT:
                MessageTextReceptorHolder vh2 = (MessageTextReceptorHolder) holder;
                vh2.bind(message, listener);
                break;
            case TYPE_EMISOR_IMAGE:
                MessageImageEmisorHolder vh3 = (MessageImageEmisorHolder) holder;
                vh3.bind(message, listener, MessageTextEmisorHolder.getDrawableFromMessageStatus(message.getMessageStatus(), context), context);
                break;
            case TYPE_RECEPTOR_IMAGE:
                MessageImageReceptorHolder vh4 = (MessageImageReceptorHolder) holder;
                vh4.bind(message, listener, context);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void onMessageAdded(ChatMessage message) {
        if (messages.contains(message)) {
            updateMessage(message);
        } else {
            addMessage(message);
        }
    }

    private void addMessage(ChatMessage message) {
        Log.d(TAG, this.hashCode() + ", addMessage message: " + message);
        messages.add(message);
        notifyItemInserted(getItemCount());
        scrollToLastItem();
    }

    private void updateMessage(ChatMessage message) {
        Log.d(TAG, this.hashCode() + ", updateMessage message: " + message);
        int position = messages.indexOf(message);
        messages.set(position, message);
        notifyItemChanged(position);
        scrollToLastItem();
    }

    public void onMessagedChanged(ChatMessage message) {
        if (messages.contains(message)) {
            updateMessage(message);
        } else {
            addMessage(message);
        }

    }

    public void addMessageList(List<ChatMessage> messages) {
        Log.d(TAG, "addMessageList");
        if (messages != null && !messages.isEmpty()) {
            this.messages.clear();
            this.messages.addAll(messages);
            notifyDataSetChanged();
            scrollToLastItem();
        }
    }

    private void scrollToLastItem() {
        recyclerView.scrollToPosition(getItemCount() - 1);
    }
}
