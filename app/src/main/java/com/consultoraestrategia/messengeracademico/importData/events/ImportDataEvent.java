package com.consultoraestrategia.messengeracademico.importData.events;

/**
 * Created by Steve on 24/02/2017.
 */
public class ImportDataEvent {
    public static final int OnContactImported = 0;
    public static final int OnInstitutionImported = 1;
    public static final int OnContactFailedToImport = 2;
    public static final int OnInstitutionFailedToImport = 3;


    public static final int OnImportFinish = 4;
    public static final int OnImportError = 5;


    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
