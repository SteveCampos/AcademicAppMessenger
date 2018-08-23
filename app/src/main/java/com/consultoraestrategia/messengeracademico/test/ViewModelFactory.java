package com.consultoraestrategia.messengeracademico.test;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.test.firebase.UsersViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UseCaseHandler handler;

    public ViewModelFactory(UseCaseHandler handler) {
        this.handler = handler;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UsersViewModel(handler);
    }
}
