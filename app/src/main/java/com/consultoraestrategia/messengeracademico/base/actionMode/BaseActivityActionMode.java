package com.consultoraestrategia.messengeracademico.base.actionMode;

import android.support.annotation.MenuRes;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.consultoraestrategia.messengeracademico.base.BaseActivity;

public abstract class BaseActivityActionMode<O extends Selectable, V extends BaseActionModeView<P, O>, P extends BasePresenterActionMode<V, O>> extends BaseActivity<V, P> implements BaseActionModeView<P, O>, ActionMode.Callback {

    @Override
    public void onClick(O data, View view) {
        if (presenter != null) presenter.onClick(data, view);
    }

    @Override
    public void onLongClick(O data, View view) {
        if (presenter != null) presenter.onLongClick(data, view);
    }

    private ActionMode actionMode;

    private @MenuRes
    int menuRes;

    @Override
    public void createActionMode(int menuRes) {
        Log.d(getTag(), "createActionMode: ");
        this.menuRes = menuRes;
        actionMode = startSupportActionMode(this);
    }

    @Override
    public void destroyActionMode() {
        Log.d(getTag(), "destroyActionMode: ");
        if (actionMode != null) actionMode.finish();
    }

    @Override
    public void setActionModeTitle(String title) {
        Log.d(getTag(), "setActionModeTitle: ");
        if (!TextUtils.isEmpty(title) && actionMode != null) {
            actionMode.setTitle(title);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(getTag(), "onCreateActionMode: ");
        mode.getMenuInflater().inflate(menuRes, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        Log.d(getTag(), "onPrepareActionMode: ");
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        Log.d(getTag(), "onDestroyActionMode: ");
        presenter.onDestroyActionMode();
        mode.finish();
    }
}
