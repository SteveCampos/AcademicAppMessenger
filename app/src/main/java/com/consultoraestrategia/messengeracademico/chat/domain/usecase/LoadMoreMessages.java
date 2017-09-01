package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.util.List;

/**
 * Created by @stevecampos on 1/09/2017.
 */

public class LoadMoreMessages extends UseCase<LoadMoreMessages.RequestValues, LoadMoreMessages.ResponseValue> {

    private static final String TAG = LoadMoreMessages.class.getSimpleName();
    private ChatRepository repository;

    public LoadMoreMessages(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(LoadMoreMessages.RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        repository.getMoreMessages(requestValues.getMessage(), new ChatDataSource.GetMessageCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                Log.d(TAG, "onMessagesLoaded");
                if (messages != null && !messages.isEmpty()) {
                    LoadMoreMessages.ResponseValue responseValue = new LoadMoreMessages.ResponseValue(messages);
                    getUseCaseCallback().onSuccess(responseValue);
                }
            }

            @Override
            public void onDataNotAvailable() {
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

        private final List<ChatMessage> messages;

        public ResponseValue(@NonNull List<ChatMessage> messages) {
            this.messages = messages;
        }

        public List<ChatMessage> getMessages() {
            return messages;
        }
    }
}