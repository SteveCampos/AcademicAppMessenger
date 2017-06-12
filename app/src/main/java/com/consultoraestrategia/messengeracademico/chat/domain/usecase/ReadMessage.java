package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 22/05/2017.
 */

public class ReadMessage extends UseCase<ReadMessage.RequestValues, ReadMessage.ResponseValue> {

    private static final String TAG = ReadMessage.class.getSimpleName();
    private ChatRepository repository;

    public ReadMessage(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ChatMessage message = requestValues.getMessage();
        repository.saveMessageWithStatusReaded(message, null,
                new ChatDataSource.ListenMessagesCallback() {
                    @Override
                    public void onMessageChanged(ChatMessage message) {
                        Log.d(TAG, "onMessageChanged");
                        ResponseValue responseValue = new ResponseValue(message);
                        //getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "error: " + error);
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
