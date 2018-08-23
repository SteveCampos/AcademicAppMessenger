package com.consultoraestrategia.messengeracademico.base.actionMode;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;

public interface BasePresenterActionMode<V extends BaseActionModeView, O extends Selectable> extends BasePresenter<V>, SelectListener<O> {
    void onDestroyActionMode();
}
