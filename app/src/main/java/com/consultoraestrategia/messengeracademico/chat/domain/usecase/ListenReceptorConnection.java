package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Connection;

/**
 * Created by @stevecampos on 29/05/2017.
 */

public class ListenReceptorConnection extends UseCase<ListenReceptorConnection.RequestValues, ListenReceptorConnection.ResponseValue> {

    private static final String TAG = ListenReceptorConnection.class.getSimpleName();
    private ChatRepository repository;

    public ListenReceptorConnection(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        repository.listenConnection(requestValues.getChat(), new ChatDataSource.ListenConnectionCallback() {
            @Override
            public void onConnectionChanged(Connection connection) {
                getUseCaseCallback().onSuccess(new ResponseValue(connection));
            }

            @Override
            public void onError(String error) {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Connection connection;

        public ResponseValue(Connection connection) {
            this.connection = connection;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private Chat chat;

        public RequestValues(Chat chat) {
            this.chat = chat;
        }

        public Chat getChat() {
            return chat;
        }
    }
}
