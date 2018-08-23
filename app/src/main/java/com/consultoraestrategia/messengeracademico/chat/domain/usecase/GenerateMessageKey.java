package com.consultoraestrategia.messengeracademico.chat.domain.usecase;

import android.text.TextUtils;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 16/06/2017.
 */

public class GenerateMessageKey extends UseCase<GenerateMessageKey.RequestValues, GenerateMessageKey.ResponseValue> {

    private final FirebaseChat firebaseChat;

    public GenerateMessageKey(FirebaseChat firebaseChat) {
        this.firebaseChat = firebaseChat;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Contact emisor = requestValues.getEmisor();
        Contact receptor = requestValues.getReceptor();

        String keyMessage = firebaseChat.getKeyMessage(emisor, receptor);
        if (!TextUtils.isEmpty(keyMessage)) {
            getUseCaseCallback().onSuccess(new ResponseValue(keyMessage));
        } else {
            getUseCaseCallback().onError();
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final String key;

        public ResponseValue(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
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

}
