package com.consultoraestrategia.messengeracademico.chat.domain.usecase;


import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

public class DeleteMessage extends UseCase<DeleteMessage.RequestValues, DeleteMessage.ResponseValue> {

    private static final String TAG = ReadMessage.class.getSimpleName();
    private ChatRepository repository;

    public DeleteMessage(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(DeleteMessage.RequestValues requestValues) {
        ChatMessage message = requestValues.getMessage();
        repository.deleteMessage(message, new ChatDataSource.SuccessCallback<ChatMessage>() {
            @Override
            public void onSucess(ChatMessage data, boolean success) {
                if (success) {
                    getUseCaseCallback().onSuccess(new ResponseValue(data));
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
