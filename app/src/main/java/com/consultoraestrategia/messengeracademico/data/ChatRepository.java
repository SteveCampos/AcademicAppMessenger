package com.consultoraestrategia.messengeracademico.data;

import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.crme_educativo.api.CrmeApiImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseGetInfo;
import com.consultoraestrategia.messengeracademico.data.source.local.ChatLocalDataSource;
import com.consultoraestrategia.messengeracademico.data.source.remote.ChatRemoteDataSource;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEvent;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEvent;
import com.consultoraestrategia.messengeracademico.utils.PhonenumberUtils;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.android.gms.common.util.CrashUtils;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class ChatRepository implements ChatDataSource {

    private static final String TAG = ChatRepository.class.getSimpleName();
    private static ChatRepository INSTANCE = null;
    private ChatLocalDataSource chatLocalDataSource;
    private ChatRemoteDataSource chatRemoteDataSource;
    private ChatListPostEvent chatListPostEvent;
    private ChatPostEvent chatPostEvent;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, ChatMessage> mCachedMessages;
    Map<String, Chat> mCachedChats;
    Map<String, ChatMessage> mCachedStatusMessages;

    /**
     *
     */
    Chat chat;
    FirebaseUser mainUser;
    Contact receptor;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;
    private boolean isListenForIncomingMessages = false;

    // Prevent direct instantiation.
    private ChatRepository(ChatLocalDataSource chatLocalDataSource, ChatRemoteDataSource chatRemoteDataSource, ChatListPostEvent chatListPostEvent, ChatPostEvent chatPostEvent, FirebaseUser mainUser) {
        this.chatLocalDataSource = chatLocalDataSource;
        this.chatRemoteDataSource = chatRemoteDataSource;
        this.chatListPostEvent = chatListPostEvent;
        this.chatPostEvent = chatPostEvent;
        this.mainUser = mainUser;
        listenForAllUserMessages(null, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param chatLocalDataSource  the backend data source
     * @param chatRemoteDataSource the device storage data source
     * @return the {@link ChatRepository} instance
     */
    public static ChatRepository getInstance(ChatLocalDataSource chatLocalDataSource,
                                             ChatRemoteDataSource chatRemoteDataSource,
                                             ChatListPostEvent chatListPostEvent,
                                             ChatPostEvent chatPostEvent,
                                             FirebaseUser mainUser) {
        if (INSTANCE == null) {
            INSTANCE = new ChatRepository(chatLocalDataSource, chatRemoteDataSource, chatListPostEvent, chatPostEvent, mainUser);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(ChatLocalDataSource, ChatRemoteDataSource, ChatListPostEvent, ChatPostEvent, FirebaseUser)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public void deleteChat(Chat chat, SuccessCallback<Chat> successCallback) {
        Log.d(TAG, "deleteChat: ");
        chatLocalDataSource.deleteChat(chat, successCallback);
        chatRemoteDataSource.deleteChat(chat, successCallback);
    }

    @Override
    public void getChat(final Contact emisor, final Contact receptor, final GetChatCallback callback) {
        Log.d(TAG, "getChat");
        if (this.receptor == null || this.mainUser == null || !this.receptor.equals(receptor)) {
            mCachedMessages = null;
        }
        //this.mainUser = emisor;
        this.receptor = receptor;
        String orderedKeys[] = StringUtils.sortAlphabetical(mainUser.getUid(), receptor.getUid());
        chatLocalDataSource.getChat(emisor, receptor, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chatLoaded) {
                chat = chatLoaded;
                addChatToCache(chatLoaded);
                callback.onChatLoaded(chatLoaded);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "Fatal error! chatLocalDataSource.getChat failed!");
            }
        });
        /*getChat(orderedKeys[0] + "_" + orderedKeys[1], new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chatLoaded) {
                chat = chatLoaded;
                addChatToCache(chatLoaded);
                callback.onChatLoaded(chatLoaded);
            }

            @Override
            public void onDataNotAvailable() {
                chatLocalDataSource.getChat(emisor, receptor, new GetChatCallback() {
                    @Override
                    public void onChatLoaded(Chat chatLoaded) {
                        chat = chatLoaded;
                        addChatToCache(chatLoaded);
                        callback.onChatLoaded(chatLoaded);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.d(TAG, "Fatal error! chatLocalDataSource.getChat failed!");
                    }
                });
            }
        });*/
    }

    @Override
    public void getChat(final String keyChat, final GetChatCallback callback) {
        Log.d(TAG, "getChat keyChat: " + keyChat);

        // Respond immediately with cache if available and not dirty
        Chat cachedChat = getCachedChat(keyChat);
        if (cachedChat != null && !mCacheIsDirty) {
            callback.onChatLoaded(cachedChat);
        } else {
            //getChat from local source!
            chatLocalDataSource.getChat(keyChat, new GetChatCallback() {
                @Override
                public void onChatLoaded(Chat chatLoaded) {
                    //chat = chatLoaded;
                    addChatToCache(chatLoaded);
                    callback.onChatLoaded(chatLoaded);
                }

                @Override
                public void onDataNotAvailable() {
                    Log.d(TAG, "chatLocalDataSource.getChat onDataNotAvailable");
                    //getChat from remote source!
                    //callback.onDataNotAvailable();
                    getChatFromRemote(keyChat, new GetChatCallback() {
                        @Override
                        public void onChatLoaded(Chat chat) {
                            chat.save();
                            addChatToCache(chat);
                            fireChat(chat);
                        }

                        @Override
                        public void onDataNotAvailable() {
                            Log.d(TAG, "getChatFromRemote onDataNotAvailable: ");
                        }
                    });
                }
            });
        }

    }

    public void getChatFromRemote(String keyChat, GetChatCallback callback) {
        Log.d(TAG, "getChatFromRemote: " + keyChat);
        chatRemoteDataSource.getChat(keyChat, callback);
    }

    @Override
    public void getChat(final ChatMessage message, final GetChatCallback callback) {
        getChat(message.getChatKey(), new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                addChatToCache(chat);
                callback.onChatLoaded(chat);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "getChat onDataNotAvailable constructing new chat...");
                chatLocalDataSource.getChat(message, new GetChatCallback() {
                    @Override
                    public void onChatLoaded(Chat chat) {
                        callback.onChatLoaded(chat);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.d(TAG, "getChat from message onDataNotAvailable!!! imposible to recover chat!!");
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    /**
     * Gets messages from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link GetMessageCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getMessages(final Chat chat, final GetMessageCallback callback) {
        // Respond immediately with cache if available and not dirty
        if (mCachedMessages != null && !mCacheIsDirty) {
            Log.d(TAG, "getMessages from mCachedMessages");
            callback.onMessagesLoaded(new ArrayList<>(mCachedMessages.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            Log.d(TAG, "getMessages from chatRemoteDataSource");
            chatRemoteDataSource.getMessages(chat, callback);
        } else {
            // Query the local storage if available. If not, query the network.
            Log.d(TAG, "getMessages from chatLocalDataSource");
            chatLocalDataSource.getMessages(chat, new GetMessageCallback() {
                @Override
                public void onMessagesLoaded(List<ChatMessage> messages) {
                    Log.d(TAG, "onMessagesLoaded");
                    Collections.sort(messages, new Comparator<ChatMessage>() {
                        @Override
                        public int compare(ChatMessage o1, ChatMessage o2) {
                            return Long.valueOf(o1.getTimestamp()).compareTo(o2.getTimestamp());
                        }
                    });
                    callback.onMessagesLoaded(messages);
                    refreshCache(messages);
                }

                @Override
                public void onDataNotAvailable() {
                    chatRemoteDataSource.getMessages(chat, callback);
                }
            });
        }
    }

    /**
     * Gets more messages from local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link GetMessageCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getMoreMessages(final ChatMessage message, final GetMessageCallback callback) {
        /*
        // Respond immediately with cache if available and not dirty
        if (mCachedMessages != null && !mCacheIsDirty) {
            Log.d(TAG, "getMessages from mCachedMessages");
            callback.onMessagesLoaded(new ArrayList<>(mCachedMessages.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            Log.d(TAG, "getMessages from chatRemoteDataSource");
            chatRemoteDataSource.getMessages(chat, callback);
        } else {
            */
        // Query the local storage if available. If not, query the network.
        Log.d(TAG, "getMoreMessages from chatLocalDataSource");
        chatLocalDataSource.getMoreMessages(message, new GetMessageCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                Log.d(TAG, "getMoreMessages onMessagesLoaded");

                if (messages == null || messages.isEmpty()) {
                    return;
                }

                Log.d(TAG, "getMoreMessages size: " + messages.size());

                Collections.sort(messages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return Long.valueOf(o1.getTimestamp()).compareTo(o2.getTimestamp());
                    }
                });
                callback.onMessagesLoaded(messages);
                addMessagesToCache(messages);
            }

            @Override
            public void onDataNotAvailable() {
                chatRemoteDataSource.getMoreMessages(message, callback);
            }
        });
    }


    @Override
    public void getMessagesNoReaded(final Chat chat, final GetMessageCallback callback) {
        // Query the local storage if available. If not, query the network.
        Log.d(TAG, "getMessages from chatLocalDataSource");
        chatLocalDataSource.getMessagesNoReaded(chat, new GetMessageCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                Log.d(TAG, "onMessagesLoaded");
                Collections.sort(messages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return Long.valueOf(o1.getTimestamp()).compareTo(o2.getTimestamp());
                    }
                });
                //refreshCache(messages);
                callback.onMessagesLoaded(messages);
            }

            @Override
            public void onDataNotAvailable() {
                chatRemoteDataSource.getMessages(chat, callback);
            }
        });
    }


    /**
     * Send Message, save message to local and remote data source.
     * <p>
     * Note: {@link ListenMessagesCallback#onMessageChanged(ChatMessage)} is fired when message is saved to local source.
     * Note: {@link ListenMessagesCallback#onError(String)} is fired when message is saved to remote source.
     */

    /*Considerando cambiar el nombre, a sendMessage*/
    @Override
    public void saveMessage(final ChatMessage message, final Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveMessage");
        //callbackMessage(message, callback);
        if (chat != null) {
            save(message, chat, callback);
        } else {
            getChat(message, new GetChatCallback() {
                @Override
                public void onChatLoaded(Chat chat) {
                    if (chat != null) {
                        save(message, chat, callback);
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    Log.d(TAG, "save Message failed!");
                }
            });
        }
    }

    private void saveMessageLocal(ChatMessage message, Chat chat, final ListenMessagesCallback callback) {
        message.setMessageStatus(ChatMessage.STATUS_DELIVERED);
        chatLocalDataSource.saveMessage(message, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "onMessageChanged");
                //callbackMessage(message, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }


    private void save(ChatMessage message, Chat chat, final ListenMessagesCallback callback) {
        saveMessageLocal(message, chat, callback);
        chatRemoteDataSource.saveMessage(message, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                //callbackMessage(message, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    @Override
    public void saveMessageWithStatusWrited(final ChatMessage message, final boolean receptorOnline, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveMessageWithStatusWrited");
        //callbackMessage(message, callback);
        fireMessage(message);
        getChat(message, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                saveWithStatusWrited(message, receptorOnline, chat, callback);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "getChat FAILED!");
            }
        });
    }

    private void saveWithStatusWrited(ChatMessage message, boolean receptorOnline, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "saveWithStatusWrited");
        chatLocalDataSource.saveMessageWithStatusWrited(message, receptorOnline, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "chatLocalDataSource saveMessageWithStatusWrited onMessageChanged");
                //callback.onMessageChanged(message);
                //callbackMessage(message, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
        sendMessageWritedToRemote(message, receptorOnline, chat, callback);
    }

    @Override
    public void sendMessageNotReaded(final ChatMessage message, final boolean receptorOnline, Chat chat, final ListenMessagesCallback callback) {
        Log.d(TAG, "sendMessageNotReaded");
        getChat(message, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                sendMessageWritedToRemote(message, receptorOnline, chat, callback);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "getChat FAILED!");
            }
        });
    }

    private void sendMessageWritedToRemote(ChatMessage message, boolean receptorOnline, Chat chat, final ListenMessagesCallback callback) {
        chatRemoteDataSource.saveMessageWithStatusWrited(message, receptorOnline, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "chatRemoteDataSource saveMessageWithStatusWrited onMessageChanged");
                //callbackMessage(message, callback);
                message.setMessageStatus(ChatMessage.STATUS_SEND);
                fireMessage(message);
                callback.onMessageChanged(message);
                saveMessageOnLocal(message);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }


    @Override
    public void saveMessageWithStatusReaded(final ChatMessage message, Chat chat, final ListenMessagesCallback callback) {
        message.setMessageStatus(ChatMessage.STATUS_READED);
        Log.d(TAG, "saveMessageWithStatusReaded");
        if (chat != null) {
            saveWithStatusReaded(message, chat);
        } else {
            getChat(message, new GetChatCallback() {
                @Override
                public void onChatLoaded(Chat chat) {
                    if (chat != null) {
                        saveWithStatusReaded(message, chat);
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    Log.d(TAG, "save Message failed!");
                }
            });
        }
    }

    private void saveWithStatusReaded(ChatMessage message, Chat chat) {
        chatLocalDataSource.saveMessageWithStatusReaded(message, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "saveWithStatusReaded onMessageChanged");
                //callbackMessage(message, callback);
                fireMessage(message);
            }

            @Override
            public void onError(String error) {
                //callback.onError(error);
            }
        });

        chatRemoteDataSource.saveMessageWithStatusReaded(message, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                //callbackMessage(message, callback);
            }

            @Override
            public void onError(String error) {
                //callback.onError(error);
            }
        });

    }

    private void fireChat(final ChatMessage message) {
        Log.d(TAG, "fireChat: ");
        getChat(message, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                Log.d(TAG, "getChat onChatLoaded: " + chat);
                if (chat != null) {
                    fireChat(chat);
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "fireChat getChat onDataNotAvailable!!! ");
            }
        });
    }

    private void fireChat(Chat chat) {
        chatListPostEvent.post(chat);
    }

    private void fireMessage(ChatMessage message) {
        Log.d(TAG, "fireMessage");
        post(message);
        fireChat(message);
    }

    private void post(ChatMessage message) {
        chatPostEvent.post(message);
    }


    /*METHOD TO UPDATE MESSAGES CACHED*/

    private void clearCache() {
        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        mCachedMessages.clear();
    }

    private void addMessagesToCache(List<ChatMessage> messages) {
        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        for (ChatMessage message : messages) {
            mCachedMessages.put(message.getId(), message);
        }
        mCacheIsDirty = false;
        sortCache();
    }

    private void removeMessageFromCache(ChatMessage message) {
        Log.d(TAG, "removeMessageFromCache: ");
        if (mCachedMessages == null) return;
        boolean isInCache = isMessageInCache(message);
        Log.d(TAG, "isInCache: " + isInCache);
        if (isInCache) {
            mCachedMessages.remove(message.getId());
        }
    }

    private void sortCache() {
        List<ChatMessage> chatMessages = new ArrayList<>(mCachedMessages.values());
        Collections.sort(chatMessages, new Comparator<ChatMessage>() {
            @Override
            public int compare(ChatMessage o1, ChatMessage o2) {
                return Long.valueOf(o1.getTimestamp()).compareTo(o2.getTimestamp());
            }
        });
        refreshCache(chatMessages);
    }

    private void refreshCache(List<ChatMessage> messages) {
        clearCache();
        for (ChatMessage message : messages) {
            mCachedMessages.put(message.getId(), message);
        }
        mCacheIsDirty = false;
    }

    private void addMessageToCache(ChatMessage message) {
        // Do in memory cache update to keep the app UI up to date
        if (mCachedMessages == null) {
            mCachedMessages = new LinkedHashMap<>();
        }
        if (chat != null && message != null && chat.getChatKey().equals(message.getChatKey())) {
            mCachedMessages.put(message.getId(), message);
        } else {
            //is a message   from another chat, fire a notification!
            Log.d(TAG, "is a message from another chat, fire a notification!");
            fireNotification(message);
        }
    }

    private Map<String, Integer> mCachedNotifications;

    private void addNotificationToCache(ChatMessage message) {
        // Do in memory cache update to keep the app UI up to date
        if (mCachedNotifications == null) {
            mCachedNotifications = new LinkedHashMap<>();
        }
        mCachedNotifications.put(message.getId(), 1);
    }

    private boolean isNotificationInCache(ChatMessage message) {
        return mCachedNotifications != null && mCachedNotifications.containsKey(message.getId());
    }

    private void fireNotification(ChatMessage message) {
        if (!isNotificationInCache(message)) {
            addNotificationToCache(message);
            fireMessageToMainEvent(message);
        }
    }

    private void fireMessageToMainEvent(ChatMessage message) {
        Log.d(TAG, "fireMessageToMainEvent: " + message.getId());
        MainEvent event = new MainEvent();
        event.setType(MainEvent.TYPE_FIRE_NOTIFICATION);
        event.setMessage(message);
        GreenRobotEventBus.getInstance().post(event);
    }

    private boolean isMessageInCache(ChatMessage message) {
        boolean isInCache = false;
        if (mCachedMessages == null || mCachedMessages.isEmpty()) {
            isInCache = false;
        } else {
            ChatMessage cachedMessage = mCachedMessages.get(message.getId());
            if (cachedMessage != null) {
                isInCache = true;
            }
        }
        return isInCache;
    }

    /*METHOD TO UPDATE CHATS CACHED*/

    private void refreshChatCached(List<Chat> chats) {
        if (mCachedChats == null) {
            mCachedChats = new LinkedHashMap<>();
        }
        mCachedChats.clear();
        for (Chat chat : chats) {
            mCachedChats.put(chat.getChatKey(), chat);
        }
        mCacheIsDirty = false;
    }

    private void addChatToCache(Chat chat) {
        // Do in memory cache update to keep the app UI up to date
        if (mCachedChats == null) {
            mCachedChats = new LinkedHashMap<>();
        }
        mCachedChats.put(chat.getChatKey(), chat);
    }

    private boolean isChatInCache(Chat chat) {
        boolean isInCache = false;
        if (mCachedChats == null || mCachedChats.isEmpty()) {
            isInCache = false;
        } else {
            Chat cachedChat = mCachedChats.get(chat.getChatKey());
            if (cachedChat != null) {
                isInCache = true;
            }
        }
        return isInCache;
    }

    private Chat getCachedChat(String chatKey) {
        if (mCachedChats != null && !mCachedChats.isEmpty() && mCachedChats.containsKey(chatKey)) {
            return mCachedChats.get(chatKey);
        }
        return null;
    }

    @Override
    public void listenForAllUserMessages(String lastMessageKey, final ListenMessagesCallback callback) {
        Log.d(TAG, "listenForAllUserMessages");
        if (!isListenForIncomingMessages) {
            isListenForIncomingMessages = true;
            getLastMessage(new ListenMessagesCallback() {
                @Override
                public void onMessageChanged(ChatMessage message) {
                    Log.d(TAG, "message: " + message.toString());
                    listenMessages(message.getKeyMessage());
                }

                @Override
                public void onError(String error) {
                    listenMessages("");
                }
            });
        }
    }

    private void listenMessages(String lastKey) {
        chatRemoteDataSource.listenForAllUserMessages(lastKey, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                onIncomingMessageChanged(message);
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "listenMessages onError: " + error);
            }
        });
    }

    private void onIncomingMessageChanged(ChatMessage message) {
        Log.d(TAG, "onMessageChanged");
        Log.d(TAG, "message: " + message.toString());
        int status = message.getMessageStatus();
        switch (status) {
            case ChatMessage.STATUS_WRITED:
                handleMessageWithStatusWrited(message);
                break;
            case ChatMessage.STATUS_DELIVERED:
                handleMessageWithStatusDelivered(message);
                break;
            case ChatMessage.STATUS_READED:
                handleMessageWithStatusReaded(message);
                break;
        }
        addMessageToCache(message);
    }

    private void handleMessageWithStatusReaded(ChatMessage message) {
        Log.d(TAG, "handleMessageWithStatusReaded");
        if (iAmEmisor(message)) {
            onEmisorReceiveMessageReaded(message);
        } else {
            onReceptorReceiveMessageReaded(message);
        }
    }

    private void onEmisorReceiveMessageReaded(ChatMessage message) {
        Log.d(TAG, "onEmisorReceiveMessageReaded");
        //fire!
        fireMessage(message);
        //save on local!
        saveMessageOnLocal(message);
    }

    private void onReceptorReceiveMessageReaded(ChatMessage message) {
        Log.d(TAG, "onEmisorReceiveMessageReaded");
        //do nothing!
        fireMessage(message);
        //save on local!
        saveMessageOnLocal(message);
    }

    private void handleMessageWithStatusWrited(ChatMessage message) {
        Log.d(TAG, "onMessageStatusWritedChanged");
        if (iAmEmisor(message)) {
            onEmisorReceiveMessageWrited(message);
        } else {
            onReceptorReceiveMessageWrited(message);
        }
    }

    private void handleMessageWithStatusDelivered(ChatMessage message) {
        Log.d(TAG, "handleMessageWithStatusDelivered");
        if (iAmEmisor(message)) {
            onEmisorReceiveMessageDelivered(message);
        } else {
            onReceptorReceiveMessageDelivered(message);
        }
    }

    private void onEmisorReceiveMessageDelivered(ChatMessage message) {
        Log.d(TAG, "onEmisorReceiveMessageDelivered");
        //fire!
        fireMessage(message);
        //save on local
        saveMessageOnLocal(message);
    }

    private void onReceptorReceiveMessageDelivered(ChatMessage message) {
        Log.d(TAG, "onReceptorReceiveMessageDelivered");
        //do nothing!
        //fire
        fireMessage(message);
        //save on local
        saveMessageOnLocal(message);
    }


    private void onEmisorReceiveMessageWrited(ChatMessage message) {
        Log.d(TAG, "onEmisorReceiveMessageWrited");
        message.setMessageStatus(ChatMessage.STATUS_SEND);
        //fire
        fireMessage(message);
        //save on local
        saveMessageOnLocal(message);
    }

    public void getCrmeUserFromPhoneNumber(final String phoneNumber, final FirebaseHelper.CompletionListener<CrmeUser> listener) {
        Log.d(TAG, "getCrmeUserFromPhoneNumber: " + phoneNumber);
        String myPhone = mainUser.getPhoneNumber();
        String myPhoneNumberFormatted = PhonenumberUtils.formatPhonenumber("PE", myPhone);
        final String observadoFormatted = PhonenumberUtils.formatPhonenumber("PE", phoneNumber);

        CrmeApiImpl api = CrmeApiImpl.getInstance();
        api.getInfo(myPhoneNumberFormatted, observadoFormatted, new CrmeApiImpl.Listener<ResponseGetInfo>() {
            @Override
            public void onSuccess(ResponseGetInfo data) {
                Log.d(TAG, "getCrmeUserFromPhoneNumber onSuccess: ");
                String info = data.getValue();
                if (TextUtils.isEmpty(info) || !data.getSuccessful()) {
                    listener.onFailure(new Exception("Info is empty!!!"));
                    return;
                }

                CrmeUser crmeUser = new CrmeUser();
                crmeUser.setPhoneNumber(observadoFormatted);

                String[] nombres = info.split("-");
                int infoSize = nombres.length;
                Log.d(TAG, "infoSize: " + infoSize);
                if (infoSize < 3) {
                    listener.onFailure(new Exception("El tamaño de la información no es la adecuada "));
                    return;
                }

                crmeUser.setId(nombres[0]);
                crmeUser.setName(nombres[1]);
                crmeUser.setDisplayName(nombres[2]);
                boolean saved = crmeUser.save();

                if (saved) {
                    listener.onSuccess(crmeUser);
                } else {
                    listener.onFailure(new Exception("Failed to save crmeUser!!!"));
                }
            }

            @Override
            public void onFailure(Exception ex) {
                Log.e(TAG, "getCrmeUserFromPhoneNumber onFailure: ");
                if (listener != null) {
                    listener.onFailure(ex);
                }
            }
        });

    }


    private void getCrmeUser(final ChatMessage message, String phoneNumber) {
        getCrmeUserFromPhoneNumber(phoneNumber, new FirebaseHelper.CompletionListener<CrmeUser>() {
            @Override
            public void onSuccess(CrmeUser data) {
                chatLocalDataSource.getChat(message, new GetChatCallback() {
                    @Override
                    public void onChatLoaded(Chat chat) {
                        saveMessageOnLocal(message, chat);
                        chatLocalDataSource.saveMessage(message, chat, new ListenMessagesCallback() {
                            @Override
                            public void onMessageChanged(ChatMessage message) {
                                Log.d(TAG, "getCrmeUser getCrmeUserFromPhoneNumber getChat saveMessag onMessageChanged: ");
                                fireMessage(message);
                            }

                            @Override
                            public void onError(String error) {
                                Log.d(TAG, "getCrmeUser getCrmeUserFromPhoneNumber getChat saveMessag onError: " + error);
                            }
                        });
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.d(TAG, "getChat from message onDataNotAvailable!!! imposible to recover chat!!");

                    }
                });
                /*saveMessage(message, null, new ListenMessagesCallback() {
                    @Override
                    public void onMessageChanged(ChatMessage message) {
                        Log.d(TAG, "getCrmeUser saveMessage onMessageChanged");
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "getCrmeUser saveMessage onError");
                    }
                });*/
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    private void onReceptorReceiveMessageWrited(ChatMessage message) {
        Log.d(TAG, "onReceptorReceiveMessageWrited");

        message.setMessageStatus(ChatMessage.STATUS_DELIVERED);

        //Cuando me llegue un mensaje, validaré si el emisor o el receptor exista en mis contactos
        //si no existe, entonces consumiré un servicio para saber quién es el usuario.
        boolean existsEmisor = message.getEmisor().exists();
        boolean existsReceptor = message.getReceptor().exists();

        Log.d(TAG, "existsEmisor: " + existsEmisor);
        Log.d(TAG, "existsReceptor: " + existsReceptor);

        if (!existsEmisor && message.getEmisor().getPhoneNumber() != null) {
            getCrmeUser(message, message.getEmisor().getPhoneNumber());
            return;
        }

        if (!existsReceptor && message.getReceptor().getPhoneNumber() != null) {
            getCrmeUser(message, message.getReceptor().getPhoneNumber());
            return;
        }

        //fire!
        fireMessage(message);
        //save on local and remote!
        saveMessage(message, null, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "onReceptorReceiveMessageWrited onMessageChanged");
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "onReceptorReceiveMessageWrited onError");
            }
        });
    }

    private void saveMessageOnLocal(final ChatMessage message) {
        Log.d(TAG, "saveMessageOnLocal");
        if (chat != null) {
            saveMessageOnLocal(message, chat);
        } else {
            getChat(message, new GetChatCallback() {
                @Override
                public void onChatLoaded(Chat chat) {
                    if (chat != null) {
                        saveMessageOnLocal(message, chat);
                    }
                }

                @Override
                public void onDataNotAvailable() {
                    Log.d(TAG, "save Message failed!");
                }
            });
        }
    }

    private void saveMessageOnLocal(ChatMessage message, Chat chat) {
        chatLocalDataSource.saveMessage(message, chat, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "onMessageChanged");
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "saveMessageOnLocal error: " + error);
            }
        });
    }

    @Override
    public void stopListenMessages() {
        Log.d(TAG, "stopListenMessages");
        //chatRemoteDataSource.stopListenMessages();
    }

    @Override
    public void changeStateWriting(Chat chat, boolean writing) {
        Log.d(TAG, "changeStateWriting");
        chatRemoteDataSource.changeStateWriting(chat, writing);
    }

    @Override
    public void listenConnection(Contact contact, ListenConnectionCallback callback) {
        Log.d(TAG, "listenConnection");
        chatRemoteDataSource.listenConnection(contact, callback);
    }

    @Override
    public void listenReceptorAction(Chat chat, ListenReceptorActionCallback callback) {
        Log.d(TAG, "listenReceptorAction");
        chatRemoteDataSource.listenReceptorAction(chat, callback);
    }

    @Override
    public void saveMessageOnLocal(final ChatMessage message, final Chat chat, final ListenMessagesCallback callback) {
        getChat(message, new GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                if (chat != null) {
                    saveMessageLocal(message, chat, callback);
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "save Message failed!");
            }
        });
    }

    @Override
    public void getLastMessage(final ListenMessagesCallback callback) {
        chatLocalDataSource.getLastMessage(new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "getLastMessage message: " + message.toString());
                callback.onMessageChanged(message);
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "getLastMessage onError: " + error);
                getLastMessageRemote(callback);
            }
        });
    }

    @Override
    public void listenSingleMessage(final ChatMessage message, ListenMessagesCallback callback) {
        Log.d(TAG, "listenSingleMessage message: " + message);
        chatRemoteDataSource.listenSingleMessage(message, new ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage messageChanged) {
                Log.d(TAG, "listenSingleMessage onMessageChanged message: " + messageChanged.toString());
                if (message.getMessageStatus() != messageChanged.getMessageStatus()) {
                    onIncomingMessageChanged(messageChanged);
                }
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "listenSingleMessage onError: " + error);
            }
        });
    }

    @Override
    public void deleteMessage(final ChatMessage message, final SuccessCallback<ChatMessage> listener) {
        Log.d(TAG, "removeMessage: ");
        chatLocalDataSource.deleteMessage(message, new SuccessCallback<ChatMessage>() {
            @Override
            public void onSucess(ChatMessage data, boolean success) {
                if (success) {
                    removeMessageFromCache(data);
                    fireChat(message);
                }
                listener.onSucess(data, success);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
        chatRemoteDataSource.deleteMessage(message, listener);
    }

    private void getLastMessageRemote(ListenMessagesCallback callback) {
        chatRemoteDataSource.getLastMessage(callback);
    }


    private boolean iAmEmisor(ChatMessage message) {
        return message.getEmisor().getUid().equals(mainUser.getUid());
    }

}