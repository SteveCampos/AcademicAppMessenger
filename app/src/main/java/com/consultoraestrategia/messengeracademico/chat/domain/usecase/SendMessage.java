package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 18/05/2017.
 */

public class SendMessage extends UseCase<SendMessage.RequestValues, SendMessage.ResponseValue> {

    private static final String TAG = SendMessage.class.getSimpleName();
    private ChatRepository repository;
    private FirebaseChat firebaseChat;


    public SendMessage(ChatRepository repository, FirebaseChat firebaseChat) {
        this.repository = repository;
        this.firebaseChat = firebaseChat;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        ChatMessage message = requestValues.getMessage();

        if (message.getKeyMessage() == null || TextUtils.isEmpty(message.getKeyMessage())){
            String keyMessage = firebaseChat.getKeyMessage(message.getEmisor(), message.getReceptor());
            message.setKeyMessage(keyMessage);
            repository.saveMessageWithStatusWrited(
                    message,
                    requestValues.isReceptorOnline(),
                    null, //Chat is cached in   ChatRepository
                    new ChatDataSource.ListenMessagesCallback() {
                        @Override
                        public void onMessageChanged(ChatMessage message) {
                            Log.d(TAG, "onMessageChanged");
                            sendResponse(message);
                        }

                        @Override
                        public void onError(String error) {
                            Log.d(TAG, "error: " + error);
                            getUseCaseCallback().onError();
                        }
                    });
        }else{
            repository.sendMessageNotReaded(
                    message,
                    requestValues.isReceptorOnline(),
                    null, //Chat is cached in   ChatRepository
                    new ChatDataSource.ListenMessagesCallback() {
                        @Override
                        public void onMessageChanged(ChatMessage message) {
                            Log.d(TAG, "onMessageChanged");
                            sendResponse(message);
                        }

                        @Override
                        public void onError(String error) {
                            Log.d(TAG, "error: " + error);
                            getUseCaseCallback().onError();
                        }
                    });
        }

    }

    private void sendResponse(ChatMessage message) {
        Log.d(TAG, "sendResponse");
        SendMessage.ResponseValue responseValue = new ResponseValue(message);
        getUseCaseCallback().onSuccess(responseValue);
    }

    public final static class RequestValues implements UseCase.RequestValues {
        private final ChatMessage message;
        private final boolean receptorOnline;

        public RequestValues(ChatMessage message, boolean receptorOnline) {
            this.message = message;
            this.receptorOnline = receptorOnline;
        }

        public ChatMessage getMessage() {
            return message;
        }

        public boolean isReceptorOnline() {
            return receptorOnline;
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
