package com.consultoraestrategia.messengeracademico.base.actionMode;

import android.support.annotation.MenuRes;

public interface SelectMode {
    void createActionMode(@MenuRes int menuRes);

    void destroyActionMode();

    void setActionModeTitle(String title);
}
