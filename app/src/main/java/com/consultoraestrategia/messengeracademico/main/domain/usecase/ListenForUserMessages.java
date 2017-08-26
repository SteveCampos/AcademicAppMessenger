package com.consultoraestrategia.messengeracademico.main.domain.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.data.ChatDataSource;
import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;

/**
 * Created by @stevecampos on 30/05/2017.
 */

public class ListenForUserMessages extends UseCase<ListenForUserMessages.RequestValues, ListenForUserMessages.ResponseValue> {

    private static final String TAG = ListenForUserMessages.class.getSimpleName();
    private ChatRepository repository;

    public ListenForUserMessages(ChatRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        Log.d(TAG, "executeUseCase");
        repository.listenForAllUserMessages(null, new ChatDataSource.ListenMessagesCallback() {
            @Override
            public void onMessageChanged(ChatMessage message) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

    }

    public static final class RequestValues implements UseCase.RequestValues {
    }
}
