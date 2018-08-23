package com.consultoraestrategia.messengeracademico.importGroups;

import com.consultoraestrategia.messengeracademico.base.BaseView;

public interface ImportGroupView extends BaseView<ImportGroupPresenter> {
    void enableImportBttn(boolean enabled);

    void showTxtStatus(String status);

    void showDialogToDeleteGroups(String title, String message);
}
