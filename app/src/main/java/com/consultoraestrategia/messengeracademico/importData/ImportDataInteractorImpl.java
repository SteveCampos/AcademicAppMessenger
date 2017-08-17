package com.consultoraestrategia.messengeracademico.importData;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

/**
 * Created by @stevecampos on 24/02/2017.
 */
public class ImportDataInteractorImpl implements ImportDataInteractor {
    private static final String TAG = ImportDataInteractorImpl.class.getSimpleName();
    private ImportDataRepository repository;


    public ImportDataInteractorImpl(Context context) {
        repository = new ImportDataRepositoryImpl(context);
    }

    @Override
    public void executeImportData() {
        Log.d(TAG, "executeImportData");
        repository.getData();
    }
}
