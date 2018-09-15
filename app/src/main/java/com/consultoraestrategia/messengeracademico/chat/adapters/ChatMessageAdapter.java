package com.consultoraestrategia.messengeracademico.chat.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageImageEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageImageReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextOfficialEmisorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessageTextReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.adapters.holder.MessagetextOfficialReceptorHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMISOR_TEXT = 100;
    private static final int TYPE_EMISOR_IMAGE = 101;

    private static final int TYPE_RECEPTOR_TEXT = 200;
    private static final int TYPE_RECEPTOR_IMAGE = 201;


    private static final int TYPE_EMISOR_TEXT_OFFICIAL = 301;
    private static final int TYPE_RECEPTOR_TEXT_OFFICIAL = 302;


    private static final String TAG = ChatMessageAdapter.class.getSimpleName();

    private List<ChatMessage> messages;
    private ChatMessageListener listener;
    private FirebaseUser mainUser;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private OnBottomReachedListener onBottomReachedListener;


    public interface OnBottomReachedListener {
        void onBottomReached();

        void onNotBottom();
    }

    public ChatMessageAdapter(FirebaseUser mainUser, List<ChatMessage> messages, ChatMessageListener listener, OnBottomReachedListener bottomReachedListener) {
        Log.d(TAG, "ChatMessageAdapter new Instance: " + this.hashCode());
        this.mainUser = mainUser;
        this.messages = messages;
        this.listener = listener;
        this.onBottomReachedListener = bottomReachedListener;
    }

    public void setmLinearLayoutManager(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }


    public void setRecyclerView(final RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            //recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            scrollToLastItem();
                        }
                    });
                }
            }
        });
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messagesCount = getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                int lastPosition = mLinearLayoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "messagesCount: " + messagesCount);
                Log.d(TAG, "itemCount: " + itemCount);
                Log.d(TAG, "positionStart: " + positionStart);
                Log.d(TAG, "lastVisiblePosition: " + lastVisiblePosition);
                Log.d(TAG, "lastPosition: " + lastPosition);
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messagesCount - 1) &&
                                lastVisiblePosition == (positionStart - 2))) {
                    scrollToLastItem();
                } else {
                    if (itemCount == 1) {
                        listener.onNewMessageAddedToTheBottom();
                    }
                }
            }
        });
        recyclerView.addOnScrollListener(mScrollListener);
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int visibleItemCount = mLinearLayoutManager.getChildCount();
            int totalItemCount = mLinearLayoutManager.getItemCount();
            int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                //End of list
                onBottomReachedListener.onBottomReached();
            } else {
                onBottomReachedListener.onNotBottom();
            }
        }
    };


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT)) {
            if (message.getEmisor().getUid().equals(mainUser.getUid())) {
                return TYPE_EMISOR_TEXT;
            } else {
                return TYPE_RECEPTOR_TEXT;
            }
        }
        if (message.getMessageType().equals(ChatMessage.TYPE_IMAGE)) {
            if (message.getEmisor().getUid().equals(mainUser.getUid())) {
                return TYPE_EMISOR_IMAGE;
            } else {
                return TYPE_RECEPTOR_IMAGE;
            }
        }

        if (message.getMessageType().equals(ChatMessage.TYPE_TEXT_OFFICIAL)) {
            if (message.getEmisor().getUid().equals(mainUser.getUid())) {
                return TYPE_EMISOR_TEXT_OFFICIAL;
            } else {
                return TYPE_RECEPTOR_TEXT_OFFICIAL;
            }

        }
        /*
        if (message.getMessageType().equals(ChatMessage.TYPE_RUBRO)) {
            if (message.getEmisor().getUid().equals(mainUser.getUid())) {
                return TYPE_EMISOR_RUBRO;
            } else {
                return TYPE_RECEPTOR_RUBRO;
            }

        }*/

        return TYPE_EMISOR_TEXT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            default://TYPE_EMISOR_TEXT
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
            case TYPE_EMISOR_TEXT_OFFICIAL:
                View v5 = inflater.inflate(R.layout.item_officialtext_emisor, parent, false);
                viewHolder = new MessageTextOfficialEmisorHolder(v5);
                break;
            case TYPE_RECEPTOR_TEXT_OFFICIAL:
                View v6 = inflater.inflate(R.layout.item_officialtext_receptor, parent, false);
                viewHolder = new MessagetextOfficialReceptorHolder(v6);
                break;
        }
        return viewHolder;
    }

    private void checkIfLoadMore(int position) {
        if (position == 10 && getItemCount() >= 100) {
            listener.onLoadMore(messages.get(0));
        }
    }

    private ChatMessage getPreviousMessage(int position) {
        if (position >= 1) {
            return messages.get(position - 1);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "position: " + position);
        ChatMessage message = messages.get(position);
        ChatMessage previousMessage = getPreviousMessage(position);
        checkIfLoadMore(position);

        switch (holder.getItemViewType()) {
            case TYPE_EMISOR_TEXT:
                MessageTextEmisorHolder vh1 = (MessageTextEmisorHolder) holder;
                vh1.bind(message, previousMessage, listener);
                break;
            case TYPE_RECEPTOR_TEXT:
                MessageTextReceptorHolder vh2 = (MessageTextReceptorHolder) holder;
                vh2.bind(message, previousMessage, listener);
                break;
            case TYPE_EMISOR_IMAGE:
                MessageImageEmisorHolder vh3 = (MessageImageEmisorHolder) holder;
                vh3.bind(message, previousMessage, listener);
                break;
            case TYPE_RECEPTOR_IMAGE:
                MessageImageReceptorHolder vh4 = (MessageImageReceptorHolder) holder;
                vh4.bind(message, previousMessage, listener);
                break;
            case TYPE_EMISOR_TEXT_OFFICIAL:
                MessageTextOfficialEmisorHolder vh5 = (MessageTextOfficialEmisorHolder) holder;
                vh5.bind(message, previousMessage, listener);
                break;
            case TYPE_RECEPTOR_TEXT_OFFICIAL:
                MessagetextOfficialReceptorHolder vh6 = (MessagetextOfficialReceptorHolder) holder;
                vh6.bind(message, previousMessage, listener);
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
        //Log.d(TAG, this.hashCode() + ", addMessage message: " + message);
        messages.add(message);
        notifyItemInserted(getItemCount());
        //scrollToLastItem();
    }

    public void removeMessage(ChatMessage message) {
        Log.d(TAG, "removeMessage: ");
        int index = messages.indexOf(message);
        if (index == -1) return;

        messages.remove(index);
        notifyItemRemoved(index);
    }

    private void updateMessage(ChatMessage message) {
        //Log.d(TAG, this.hashCode() + ", updateItem message: " + message);
        int position = messages.indexOf(message);
        messages.set(position, message);
        notifyItemChanged(position);
        //scrollToLastItem();
    }

    public void onMessagedChanged(ChatMessage message) {
        if (messages.contains(message)) {
            updateMessage(message);
        } else {
            addMessage(message);
        }

    }

    public void addMessageList(List<ChatMessage> messages) {
        Log.d(TAG, "addMessagesList");
        if (messages != null && !messages.isEmpty()) {
            Log.d(TAG, "messages count: " + messages.size());
            this.messages.clear();
            this.messages.addAll(messages);
            notifyDataSetChanged();
            scrollToLastItem();
        }
    }

    public void addMessages(List<ChatMessage> messages) {
        Log.d(TAG, "addMessages");
        if (messages != null && !messages.isEmpty()) {
            Log.d(TAG, "count: " + messages.size());
            this.messages.addAll(0, messages);
            notifyItemRangeInserted(0, messages.size());
        }
    }

    public void scrollToLastItem() {
        recyclerView.scrollToPosition(getItemCount() - 1);
    }

}
