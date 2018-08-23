package com.consultoraestrategia.messengeracademico.importData.ui;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.importData.ImportDataPresenter;
import com.consultoraestrategia.messengeracademico.importData.ImportDataPresenterImpl;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by @stevecampos on 24/02/2017.
 */

public interface ImportDataView extends BaseView<ImportDataPresenter> {

    void setMainUser(FirebaseUser mainUser);//

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
