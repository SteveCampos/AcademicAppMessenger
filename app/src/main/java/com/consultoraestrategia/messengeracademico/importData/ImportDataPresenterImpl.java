package com.consultoraestrategia.messengeracademico.importData;

import android.content.Context;
import android.content.SharedPreferences;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Person;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataView;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Steve on 24/02/2017.
 */

public class ImportDataPresenterImpl implements ImportDataPresenter {
    private ImportDataView view;
    private ImportDataInteractor interactor;
    private EventBus eventBus;

    private boolean importFinished = false;


    public ImportDataPresenterImpl(ImportDataView view, Context context) {
        this.view = view;
        this.interactor = new ImportDataInteractorImpl(context);
        this.eventBus = GreenRobotEventBus.getInstance();
    }

    @Override
    public void onCreate() {
        eventBus.register(this);
    }

    @Override
    public void onDestroy() {
        view = null;
        eventBus.unregister(this);
    }

    @Override
    public void importData(String phoneNumber) {
        initCounters();
        if (view != null) {
            view.showProgress();
        }
        interactor.executeImportData(phoneNumber);
    }

    private void initCounters() {
        contactsSucess = 0;

    }

    @Subscribe
    @Override
    public void onEventMainThread(ImportDataEvent event) {
        switch (event.getType()) {
            case ImportDataEvent.OnContactImported:
                onContactImported();
                break;
            case ImportDataEvent.OnInstitutionImported:
                onInstitutionImported();
                break;
            case ImportDataEvent.OnContactFailedToImport:
                onContactFailedToImport();
                break;
            case ImportDataEvent.OnInstitutionFailedToImport:
                onInstitutionFailedToImport();
                break;
            case ImportDataEvent.OnImportFinish:
                finishImport();
                break;
            case ImportDataEvent.OnImportError:
                onImportError();
                break;
            case ImportDataEvent.OnProfileRetrieved:
                onProfileRetrieved(event.getContact());
                break;
        }

    }

    private void onProfileRetrieved(Contact contact) {
        if (view != null) {
            view.setProfile(contact);
        }
    }

    private void onImportError() {
        errors++;
        if (view != null) {
            view.hideProgress();
            view.onImportError();
        }
    }

    private int errors = 0;

    private void onContactFailedToImport() {
        errors++;
        if (view != null) {
            view.hideProgress();
            view.onImportError();
        }
    }


    private int institutionError = 0;

    private void onInstitutionFailedToImport() {
        if (view != null) {
            institutionError++;
        }
    }

    private int institutionsSuccess = 0;

    private void onInstitutionImported() {
        if (view != null) {
            view.onInstitutionsImported(++institutionsSuccess);
        }
    }

    private int contactsSucess = 0;

    private void onContactImported() {
        if (view != null) {
            view.onContactsImported(++contactsSucess);
        }
    }


    @Override
    public void finishImport() {
        if (view != null) {
            view.hideProgress();
            if (errors == 0) {
                importFinished = true;
                view.onImportSuccess();
            } else {
                view.onImportError();
            }
        }
    }

    @Override
    public void handleClick(String phoneNumber) {
        if (importFinished) {
            goToMain();
        } else {
            importData(phoneNumber);
        }
    }


    @Override
    public void goToMain() {
        if (view != null) {
            if (importFinished && (errors == 0)) {
                view.goToMain(); // go to main
            } else {
                finishImport();//Show errors
            }
        }
    }
}
