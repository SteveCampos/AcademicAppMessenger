package com.consultoraestrategia.messengeracademico.importData;

import com.consultoraestrategia.messengeracademico.BasePresenter;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;

/**
 * Created by @stevecampos on 24/02/2017.
 */

public interface ImportDataPresenter extends BasePresenter {

    void onEventMainThread(ImportDataEvent event);

    void finishImport();


    void handleClick();

    void goToMain();

    void importData();
}
