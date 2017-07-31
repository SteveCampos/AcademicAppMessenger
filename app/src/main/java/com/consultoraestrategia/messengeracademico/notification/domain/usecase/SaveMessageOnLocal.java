package com.consultoraestrategia.messengeracademico.notification.domain.usecase;

import android.util.Log;

import com.amitshekhar.model.Response;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 30/06/2017.
 */

public class SaveMessageOnLocal extends UseCase<SaveMessageOnLocal.RequestValues, SaveMessageOnLocal.ResponseValue> {

    public static final String TAG = SaveMessageOnLocal.class.getSimpleName();

    private final ChatRepository repository;

    public SaveMessageOnLocal(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        ChatMessage message = requestValues.getMessage();
        message.setMessageStatus(ChatMessage.STATUS_DELIVERED);
        repository.saveMessage(
                message,
                null,
                new ChatDataSource.ListenMessagesCallback() {
                    @Override
                    public void onMessageChanged(ChatMessage message) {
                        Log.d(TAG, "onMessageChanged");
                        ResponseValue responseValue = new ResponseValue(message);
                        getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, "onError");
                    }
                }
        );
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
