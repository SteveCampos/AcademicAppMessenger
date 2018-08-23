package com.consultoraestrategia.messengeracademico.test.firebase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.test.usecase.ParseSnapshotUseCase;
import com.google.firebase.database.DataSnapshot;

public abstract class FireChildViewModel<T> extends ViewModel {
    private static final String TAG = "FireChildViewModel";

    private final FirebaseQueryLiveData firebaseQueryLiveData;
    private final MediatorLiveData<T> liveData = new MediatorLiveData<>();
    private final UseCaseHandler handler;
    private final Class<T> tClass;

    public FireChildViewModel(final UseCaseHandler handler, FirebaseQueryLiveData firebaseQueryLiveData, Class<T> tClass) {
        Log.d(TAG, "FireChildViewModel: ");
        this.handler = handler;
        this.firebaseQueryLiveData = firebaseQueryLiveData;
        this.tClass = tClass;
        startMediatorLiveData();
    }

    private void startMediatorLiveData() {
        Log.d(TAG, "startMediatorLiveData: ");
        liveData.addSource(
                firebaseQueryLiveData, new Observer<DataSnapshot>() {
                    @Override
                    public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                        //Log.d(TAG, "onChanged\n: " + dataSnapshot);
                        if (dataSnapshot == null) return;
                        handler.execute(
                                new ParseSnapshotUseCase<T>(),
                                new ParseSnapshotUseCase.RequestValues<>(dataSnapshot, tClass),
                                new UseCase.UseCaseCallback<ParseSnapshotUseCase.ResponseValue<T>>() {
                                    @Override
                                    public void onSuccess(ParseSnapshotUseCase.ResponseValue<T> response) {
                                        T child = response.getData();
                                        Log.d(TAG, "onSuccess: \n" + child.toString());
                                        liveData.setValue(child);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                }
                        );
                    }
                }
        );
    }

    @NonNull
    public LiveData<T> getDataSnapshotLiveData() {
        return liveData;
    }
}
