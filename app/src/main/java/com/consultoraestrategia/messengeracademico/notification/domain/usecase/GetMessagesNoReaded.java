package com.consultoraestrategia.messengeracademico.notification.domain.usecase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.util.List;

/**
 * Created by @stevecampos on 30/06/2017.
 */

public class GetMessagesNoReaded extends UseCase<GetMessagesNoReaded.RequestValues, GetMessagesNoReaded.ResponseValue> {
    public static final String TAG = GetMessagesNoReaded.class.getSimpleName();
    private final ChatRepository repository;

    public GetMessagesNoReaded(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        repository.getChat(requestValues.getChatKey(), new ChatDataSource.GetChatCallback() {
            @Override
            public void onChatLoaded(Chat chat) {
                Log.d(TAG, "getChat onChatLoaded");
                getMessagesNoReaded(chat);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable");
            }
        });
    }

    private void getMessagesNoReaded(Chat chat) {
        repository.getMessagesNoReaded(chat, new ChatDataSource.GetMessageCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> messages) {
                Log.d(TAG, "getMessagesNoReaded size: " + messages.size());
                ResponseValue responseValue = new ResponseValue(messages);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable");
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final String chatKey;

        public RequestValues(String chatKey) {
            this.chatKey = chatKey;
        }

        public String getChatKey() {
            return chatKey;
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
