package com.consultoraestrategia.messengeracademico.importData;

import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataView;

/**
 * Created by @stevecampos on 24/02/2017.
 */

public interface ImportDataPresenter extends BasePresenter<ImportDataView> {

    void onEventMainThread(ImportDataEvent event);

    void finishImport();


    void handleClick();

    void goToMain();

    void importData();
}
