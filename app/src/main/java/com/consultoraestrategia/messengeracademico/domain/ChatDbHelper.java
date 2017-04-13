package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.chatList.event.ChatListEvent;
import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

/**
 * Created by jairc on 24/03/2017.
 */

public class ChatDbHelper {

    private static final String TAG = ChatDbHelper.class.getSimpleName();

    public ChatDbHelper() {

    }

    public static void saveMessage(final ChatMessage message,
                                   final Chat chat,
                                   Transaction.Success success,
                                   Transaction.Error error) {
        Log.d(TAG, "saveMessage");
        DatabaseDefinition database = FlowManager.getDatabase(MessengerAcademicoDatabase.class);
        Transaction transaction = database.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                chat.save();
                message.save();
            }
        })
                .success(success)
                .error(error)
                .build();
        transaction.execute();
    }


    public static Chat getChat(Contact from, Contact to) {
        String[] sort = StringUtils.sortAlphabetical(from.getUserKey(), to.getUserKey());
        Chat chat = new Chat();

        chat.setChatKey(sort[0] + "_" + sort[1]);
        chat.load();

        Contact emisor = chat.getEmisor();
        Contact receptor = chat.getReceptor();

        if (emisor != null && receptor != null) {
            emisor.load();
            receptor.load();
        } else {
            chat.setEmisor(from);
            chat.setReceptor(to);

            return chat;
        }


        chat.setEmisor(emisor);
        chat.setReceptor(receptor);

        return chat;
    }

    public static Chat getChat(ChatMessage message) {
        Contact emisor = message.getEmisor();
        Contact receptor = message.getReceptor();


        emisor.load();
        receptor.load();


        String[] sort = StringUtils.sortAlphabetical(emisor.getUserKey(), receptor.getUserKey());
        String chatKey = sort[0] + "_" + sort[1];


        Chat chat = new Chat();
        chat.setChatKey(chatKey);
        chat.setEmisor(emisor);
        chat.setReceptor(receptor);
        chat.setStateTimestamp(message.getTimestamp());
        return chat;
    }

    public static void post(EventBus eventBus, Chat chat) {
        ChatListEvent event = new ChatListEvent();
        event.setType(ChatListEvent.TYPE_CHAT);
        if (chat != null) {
            event.setChat(chat);
        }
        eventBus.post(event);
    }

}
