package com.consultoraestrategia.messengeracademico.base.actionMode;

import com.consultoraestrategia.messengeracademico.base.BaseView;

public interface BaseActionModeView<T extends BasePresenterActionMode, O extends Selectable> extends BaseView<T>, SelectMode, SelectListener<O> {
}
