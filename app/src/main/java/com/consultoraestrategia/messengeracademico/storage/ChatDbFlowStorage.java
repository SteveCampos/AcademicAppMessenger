package com.consultoraestrategia.messengeracademico.storage;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

/**
 * Created by @stevecampos on 24/04/2017.
 */

public class ChatDbFlowStorage implements ChatStorage {

    private static final String TAG = ChatDbFlowStorage.class.getSimpleName();

    private DatabaseDefinition databaseDefinition;

    public ChatDbFlowStorage() {
        databaseDefinition = FlowManager.getDatabase(MessengerAcademicoDatabase.class);
    }

    @Override
    public void save(final ChatMessage message, Transaction.Success success, Transaction.Error error) {
        Log.d(TAG, "save");
        Transaction transaction = databaseDefinition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                message.save();
            }
        })
                .success(success)
                .error(error)
                .build();
        transaction.execute();
    }

    @Override
    public void save(final ChatMessage message, final Chat chat, Transaction.Success success, Transaction.Error error) {
        Log.d(TAG, "save");
        Transaction transaction = databaseDefinition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                message.save();
                chat.save();
                if (!chat.getEmisor().exists()) {
                    Log.d(TAG, "save emisor: " + chat.getEmisor().getPhoneNumber());
                    message.getEmisor().setType(Contact.TYPE_NOT_ADDED);
                    message.getEmisor().save();
                }
            }
        })
                .success(success)
                .error(error)
                .build();
        transaction.execute();
    }


}
