package com.consultoraestrategia.messengeracademico.importData;

import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;

/**
 * Created by Steve on 24/02/2017.
 */

public interface ImportDataPresenter {
    void onCreate();

    void onDestroy();

    void onEventMainThread(ImportDataEvent event);

    void finishImport();


    void handleClick(String phoneNumber);

    void goToMain();

    void importData(String phoneNumber);
}
