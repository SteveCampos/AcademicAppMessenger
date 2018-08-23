package com.consultoraestrategia.messengeracademico.test.usecase;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.google.firebase.database.DataSnapshot;

public class ParseSnapshotUseCase<T> extends UseCase<ParseSnapshotUseCase.RequestValues<T>, ParseSnapshotUseCase.ResponseValue<T>> {
    private static final String TAG = ParseSnapshotUseCase.class.getSimpleName();

    @Override
    protected void executeUseCase(RequestValues<T> requestValues) {
        Log.d(TAG, "executeUseCase: ");
        DataSnapshot dataSnapshot = requestValues.getDataSnapshot();
        Class<T> aClass = requestValues.gettClass();
        T data = dataSnapshot.getValue(aClass);
        if (data != null) {
            getUseCaseCallback().onSuccess(new ResponseValue<>(data));
        } else {
            getUseCaseCallback().onError();
        }
    }

    public static final class ResponseValue<T> implements UseCase.ResponseValue {
        private final T data;

        ResponseValue(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class RequestValues<T> implements UseCase.RequestValues {
        private final DataSnapshot dataSnapshot;
        private final Class<T> tClass;


        public RequestValues(DataSnapshot dataSnapshot, Class<T> tClass) {
            this.dataSnapshot = dataSnapshot;
            this.tClass = tClass;
        }

        DataSnapshot getDataSnapshot() {
            return dataSnapshot;
        }

        Class<T> gettClass() {
            return tClass;
        }
    }
}
