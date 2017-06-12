package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 18/05/2017.
 */

public class ListenMessages extends UseCase<ListenMessages.RequestValues, ListenMessages.ResponseValue> {

    private static final String TAG = ListenMessages.class.getSimpleName();
    ChatRepository repository;

    public ListenMessages(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        if (!requestValues.isListen()) {
            repository.stopListenMessages();
            return;
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private Chat chat;
        private boolean listen;

        public RequestValues(Chat chat, boolean listen) {
            this.chat = chat;
            this.listen = listen;
        }

        public Chat getChat() {
            return chat;
        }

        public boolean isListen() {
            return listen;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private ChatMessage message;

        public ResponseValue(ChatMessage message) {
            this.message = message;
        }

        public ChatMessage getMessage() {
            return message;
        }
    }

}
