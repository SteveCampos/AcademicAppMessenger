package com.consultoraestrategia.messengeracademico.importGroups;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;

public interface ImportGroupPresenter extends BasePresenter<ImportGroupView> {
    void importBttnClicked();

    void onDialogNegativeClicked();

    void onDialogPositiveClicked();

}
