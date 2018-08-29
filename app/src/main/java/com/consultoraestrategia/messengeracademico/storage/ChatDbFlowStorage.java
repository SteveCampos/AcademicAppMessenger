package com.consultoraestrategia.messengeracademico.storage;

import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.crme_educativo.api.CrmeApiImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseGetInfo;
import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEventImpl;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEventImpl;
import com.consultoraestrategia.messengeracademico.utils.PhonenumberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import dagger.BindsOptionalOf;

import static com.consultoraestrategia.messengeracademico.entities.Chat.STATE_ACTIVE;

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
                long timestamp = message.getTimestamp();

                chat.setTimestamp(timestamp);
                chat.setState(STATE_ACTIVE);
                chat.save();


                Log.d(TAG, "message.getMessageText(): " + message.getMessageText());
                Log.d(TAG, "message.getEmisor().getPhoneNumber(): " + message.getEmisor().getPhoneNumber());
                Log.d(TAG, "message.getReceptor().getPhoneNumber(): " + message.getReceptor().getPhoneNumber());
                boolean existsEmisor = message.getEmisor().exists();
                boolean existsReceptor = message.getReceptor().exists();
                Log.d(TAG, "existsEmisor: " + existsEmisor);
                Log.d(TAG, "existsReceptor: " + existsReceptor);
                if (!existsEmisor && message.getEmisor().getPhoneNumber() != null) {
                    //getCrmeUser(message, chat, message.getReceptor().getPhoneNumber());
                    message.getEmisor().setType(Contact.TYPE_NOT_ADDED);
                    message.getEmisor().save();
                }
                if (!existsReceptor && message.getReceptor().getPhoneNumber() != null) {
                    //getCrmeUser(message, chat, message.getReceptor().getPhoneNumber());
                    message.getReceptor().setType(Contact.TYPE_NOT_ADDED);
                    message.getReceptor().save();
                }

                if (message.getMediaFile() != null && !message.getMediaFile().exists()) {
                    message.getMediaFile().save();
                }

                if (message.getOfficialMessage() != null) {
                    message.getOfficialMessage().save();
                }
            }
        })
                .success(success)
                .error(error)
                .build();
        transaction.execute();
    }

    /*
    private void getCrmeUser(final ChatMessage message, final Chat chat, String phoneNumber) {
        getCrmeUserFromPhoneNumber(phoneNumber, new FirebaseHelper.CompletionListener<CrmeUser>() {
            @Override
            public void onSuccess(CrmeUser data) {
                fireMessage(message);
                fireChat(chat);
            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }

    private void fireMessage(ChatMessage message) {
        Log.d(TAG, "fireMessage");
        post(message);
    }

    private void post(ChatMessage message) {
        ChatPostEventImpl.getInstance().post(message);
    }


    private void fireChat(Chat chat) {
        ChatListPostEventImpl.getInstance().post(chat);
    }


    public void getCrmeUserFromPhoneNumber(final String phoneNumber, final FirebaseHelper.CompletionListener<CrmeUser> listener) {
        Log.d(TAG, "getCrmeUserFromPhoneNumber: " + phoneNumber);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        String myPhone = user.getPhoneNumber();
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

    */


}
