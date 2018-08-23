package com.consultoraestrategia.messengeracademico.main.domain.usecase;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.chat.domain.usecase.ReadMessage;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;

public class DeleteChat extends UseCase<DeleteChat.RequestValues, DeleteChat.ResponseValue> {

    private static final String TAG = ReadMessage.class.getSimpleName();
    private ChatRepository repository;

    public DeleteChat(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(DeleteChat.RequestValues requestValues) {
        Chat chat = requestValues.getChat();
        repository.deleteChat(chat, new ChatDataSource.SuccessCallback<Chat>() {
            @Override
            public void onSucess(Chat data, boolean success) {
                if (success) {
                    getUseCaseCallback().onSuccess(new DeleteChat.ResponseValue(data));
                    return;
                }
                onFailure(new Exception("No se pudo eliminar!!!"));
            }

            @Override
            public void onFailure(Exception e) {
                getUseCaseCallback().onError();
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
        private final Chat chat;

        public ResponseValue(Chat chat) {
            this.chat = chat;
        }

        public Chat getChat() {
            return chat;
        }
    }
}
