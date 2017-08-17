package com.consultoraestrategia.messengeracademico.importData;

import android.content.Context;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataView;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by @stevecampos on 24/02/2017.
 */

public class ImportDataPresenterImpl implements ImportDataPresenter {
    private static final String TAG = ImportDataPresenterImpl.class.getSimpleName();
    private ImportDataView view;
    private ImportDataInteractor interactor;
    private EventBus eventBus;
    private FirebaseUser mainUser;

    private boolean importFinished = false;

    public ImportDataPresenterImpl(Context context) {
        this.interactor = new ImportDataInteractorImpl(context);
        this.eventBus = GreenRobotEventBus.getInstance();
        this.mainUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void attachView(BaseView view) {
        Log.d(TAG, "onCreate");
        this.view = (ImportDataView) view;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        eventBus.register(this);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        showMainUser();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        eventBus.unregister(this);
        view = null;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
    }

    private void showMainUser() {
        Log.d(TAG, "showMainUser");
        if (view != null) {
            view.setMainUser(mainUser);
        }
    }

    @Override
    public void importData() {
        Log.d(TAG, "importData");
        initCounters();
        if (view != null) {
            view.showProgress();
        }
        interactor.executeImportData();
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
    public void handleClick() {
        if (importFinished) {
            goToMain();
        } else {
            importData();
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
