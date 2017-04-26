package com.consultoraestrategia.messengeracademico.storage;

import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

/**
 * Created by @stevecampos on 24/04/2017.
 */

public interface ChatStorage {
    void save(ChatMessage message, Transaction.Success success,
              Transaction.Error error);

    void save(ChatMessage message, Chat chat, Transaction.Success success,
              Transaction.Error error);
}
