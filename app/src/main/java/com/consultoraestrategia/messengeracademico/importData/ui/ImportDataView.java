package com.consultoraestrategia.messengeracademico.importData.ui;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Person;

/**
 * Created by Steve on 24/02/2017.
 */

public interface ImportDataView {

    void setProfile(Contact contact);//

    void showWelcomeView();
    void hideWelcomeView();

    void showImportView();
    void hideImportView();

    void onInstitutionsImported(int count);
    void onContactsImported(int count);


    void showProgress();
    void hideProgress();

    void onImportSuccess();
    void onImportError();

    void goToMain();

}
