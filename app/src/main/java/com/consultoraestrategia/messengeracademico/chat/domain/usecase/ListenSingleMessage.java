package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 25/08/2017.
 */

public class ListenSingleMessage extends UseCase<ListenSingleMessage.RequestValues, ListenSingleMessage.ResponseValue> {

    private static final String TAG = ListenSingleMessage.class.getSimpleName();
    private ChatRepository repository;

    public ListenSingleMessage(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(ListenSingleMessage.RequestValues requestValues) {
        ChatMessage message = requestValues.getMessage();
        repository.listenSingleMessage(message, new ChatDataSource.ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {
                Log.d(TAG, "onMessageChanged");
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "onError");
            }
        });
    }
    public static final class RequestValues implements UseCase.RequestValues {
        private final ChatMessage message;

        public RequestValues(ChatMessage message) {
            this.message = message;
        }

        public ChatMessage getMessage() {
            return message;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final ChatMessage message;

        public ResponseValue(ChatMessage message) {
            this.message = message;
        }

        public ChatMessage getMessage() {
            return message;
        }
    }
}
