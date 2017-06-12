package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.util.List;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class LoadMessages extends UseCase<LoadMessages.RequestValues, LoadMessages.ResponseValue> {

    private static final String TAG = LoadMessages.class.getSimpleName();
    private ChatRepository repository;

    public LoadMessages(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        repository.getMessages(requestValues.getChat(), new ChatDataSource.GetMessageCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                if (messages != null && !messages.isEmpty()) {
                    ResponseValue responseValue = new ResponseValue(messages);
                    getUseCaseCallback().onSuccess(responseValue);
                }
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Chat chat;

        public RequestValues(Chat chat) {
            this.chat = chat;
        }

        public Chat getChat() {
            return chat;
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
