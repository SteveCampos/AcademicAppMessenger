package com.consultoraestrategia.messengeracademico.storage;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

/**
 * Created by jairc on 24/04/2017.
 */

public class ChatStorageImpl implements ChatStorage {

    private ChatStorage storage;

    public ChatStorageImpl(ChatDbFlowStorage storage) {
        this.storage = storage;
    }

    @Override
    public void save(ChatMessage message, Transaction.Success success, Transaction.Error error) {
        storage.save(message, success, error);
    }

    @Override
    public void save(ChatMessage message, Chat chat, Transaction.Success success, Transaction.Error error) {
        storage.save(message, chat, success, error);
    }
}
