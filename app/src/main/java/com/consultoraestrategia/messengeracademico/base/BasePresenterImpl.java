package com.consultoraestrategia.messengeracademico.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.lib.EventBus;

public abstract class BasePresenterImpl<V extends BaseView> implements BasePresenter<V> {

    protected abstract String getTag();

    protected abstract BasePresenter<V> getPresenterImpl();

    protected V view;
    protected UseCaseHandler handler;
    protected Resources res;
    protected EventBus eventBus;

    public BasePresenterImpl(UseCaseHandler handler, Resources res, EventBus eventBus) {
        this.handler = handler;
        this.res = res;
        this.eventBus = eventBus;
    }

    protected Bundle getExtras() {
        return extras;
    }

    @Override
    public void attachView(V view) {
        Log.d(getTag(), "attachView");
        this.view = view;
    }

    @Override
    public void onCreate() {
        Log.d(getTag(), "onCreate");
    }

    @Override
    public void onStart() {
        Log.d(getTag(), "onStart");
        eventBus.register(this);
    }

    @Override
    public void onResume() {
        Log.d(getTag(), "onResume");
    }

    @Override
    public void onPause() {
        Log.d(getTag(), "onPause");
    }

    @Override
    public void onStop() {
        Log.d(getTag(), "onStop");
        eventBus.unregister(this);
    }

    @Override
    public void onDestroy() {
        Log.d(getTag(), "onDestroy");
        this.view = null;
    }

    protected Bundle extras;

    @Override
    public void setExtras(Bundle extras) {
        Log.d(getTag(), "setExtras");
        this.extras = extras;
    }

    @Override
    public void onBackPressed() {
        Log.d(getTag(), "onBackPressed");
    }


    protected void showProgress() {
        if (view != null) view.showProgress();
    }

    protected void hideProgress() {
        if (view != null) view.hideProgress();
    }

    protected void showMessage(CharSequence message) {
        if (view != null) view.showMessage(message);
    }

    protected void showMessage(@StringRes int message) {
        if (view != null) view.showMessage(message);
    }

    protected void showImportantMessage(CharSequence message) {
        if (view != null) view.showImportantMessage(message);
    }

    protected void showFinalMessage(CharSequence message) {
        if (view != null) view.showFinalMessage(message);
    }

    protected void showEdittextDialog(String text, int inputType, String title, String hint, int requestCode) {
        if (view != null) view.showEdittextDialog(text, inputType, title, hint, requestCode);
    }

}
