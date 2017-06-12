package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 16/05/2017.
 */

public class GetChat extends UseCase<GetChat.RequestValues, GetChat.ResponseValue> {

    private static final String TAG = GetChat.class.getSimpleName();
    private ChatRepository repository;

    public GetChat(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        Contact emisor = requestValues.getEmisor();
        Contact receptor = requestValues.getReceptor();

        repository.getChat(emisor, receptor,
                new ChatDataSource.GetChatCallback() {
                    @Override
                    public void onChatLoaded(Chat chat) {
                        Log.d(TAG, "onChatLoaded");
                        ResponseValue responseValue = new ResponseValue(chat);
                        getUseCaseCallback().onSuccess(responseValue);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        Log.d(TAG, "onDataNotAvailable");
                        getUseCaseCallback().onError();
                    }
                });

    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final Contact emisor;
        private final Contact receptor;

        public RequestValues(Contact emisor, Contact receptor) {
            this.emisor = emisor;
            this.receptor = receptor;
        }

        public Contact getEmisor() {
            return emisor;
        }

        public Contact getReceptor() {
            return receptor;
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
