package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 29/05/2017.
 */

public class ListenReceptorAction extends UseCase<ListenReceptorAction.RequestValues, ListenReceptorAction.ResponseValue> {

    private ChatRepository repository;

    public ListenReceptorAction(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        repository.listenReceptorAction(requestValues.getChat(), new ChatDataSource.ListenReceptorActionCallback() {
            @Override
            public void onReceptorActionChanged(Contact receptor, String action) {
                getUseCaseCallback().onSuccess(new ResponseValue(action));
            }
        });
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private String action;

        public ResponseValue(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private Chat chat;

        public RequestValues(Chat chat) {
            this.chat = chat;
        }

        public Chat getChat() {
            return chat;
        }
    }

}
