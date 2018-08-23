package com.consultoraestrategia.messengeracademico.base.actionMode;

import android.view.View;

public interface SelectListener<T> {

    void onClick(T data, View view);

    void onLongClick(T data, View view);
}
