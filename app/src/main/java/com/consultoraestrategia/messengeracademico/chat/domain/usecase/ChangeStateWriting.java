package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;

/**
 * Created by @stevecampos on 29/05/2017.
 */

public class ChangeStateWriting extends UseCase<ChangeStateWriting.RequestValues, ChangeStateWriting.ResponseValue> {

    public static final String TAG = ChangeStateWriting.class.getSimpleName();
    private ChatRepository repository;

    public ChangeStateWriting(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        repository.changeStateWriting(requestValues.getChat(), requestValues.isWriting);
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private Chat chat;
        private boolean isWriting;

        public RequestValues(Chat chat, boolean isWriting) {
            this.chat = chat;
            this.isWriting = isWriting;
        }

        public Chat getChat() {
            return chat;
        }
    }
}
