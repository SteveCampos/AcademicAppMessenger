package com.consultoraestrategia.messengeracademico.prueba.domain.usecase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.domain.FirebaseImageStorage;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

/**
 * Created by @stevecampos on 13/06/2017.
 */

public class UploadImage extends UseCase<UploadImage.RequestValues, UploadImage.ResponseValue> {
    public static final int SUCCESS = 1;
    public static final int ERROR = -1;
    public static final int PROGRESS = 0;
    private static final String TAG = UploadImage.class.getSimpleName();

    private final FirebaseImageStorage firebaseImageStorage;
    private ChatMessage message;

    public UploadImage(FirebaseImageStorage firebaseImageStorage) {
        this.firebaseImageStorage = firebaseImageStorage;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        this.message = requestValues.getImageMessage();
        if (message != null && message.getMessageUri() != null && message.getKeyMessage() != null) {
            firebaseImageStorage.
                    uploadImage(
                            Uri.parse(message.getMessageUri()),
                            new FirebaseImageStorage.FileListener() {
                                @Override
                                public void onSuccess(String url) {
                                    fireSucess(Uri.parse(url));
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    fireFailure(exception);
                                }

                                @Override
                                public void onProgress(double progress) {
                                    fireProgress(progress);
                                }
                            }
                    );
        }else{
            fireFailure(new Exception("message != null && message.getMessageUri() != null && message.getKeyMessage() != null are false!!!"));
        }
    }

    private void fire(ResponseValue responseValue) {
        getUseCaseCallback().onSuccess(responseValue);
    }

    private void fireSucess(Uri uri) {
        message.setMessageUri(uri.toString());
        fire(new ResponseValue(SUCCESS, message, 100.0, null));
    }

    private void fireProgress(double percent) {
        fire(new ResponseValue(PROGRESS, message, percent, null));
    }

    private void fireFailure(Exception e) {
        fire(new ResponseValue(ERROR, message, 0.0, e.getLocalizedMessage()));
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private final ChatMessage imageMessage;

        public RequestValues(ChatMessage imageMessage) {
            this.imageMessage = imageMessage;
        }

        public ChatMessage getImageMessage() {
            return imageMessage;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private final int type;
        private final ChatMessage message;
        private final double percent;
        private final String error;

        public ResponseValue(int type, ChatMessage message, double percent, String error) {
            this.type = type;
            this.message = message;
            this.percent = percent;
            this.error = error;
        }

        public int getType() {
            return type;
        }

        public ChatMessage getMessage() {
            return message;
        }

        public double getPercent() {
            return percent;
        }

        public String getError() {
            return error;
        }
    }
}
