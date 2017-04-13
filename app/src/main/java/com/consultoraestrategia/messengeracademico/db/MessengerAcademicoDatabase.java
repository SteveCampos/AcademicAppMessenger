package com.consultoraestrategia.messengeracademico.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Steve on 2/03/2017.
 */

@Database(name = MessengerAcademicoDatabase.NAME, version = MessengerAcademicoDatabase.VERSION)
public class MessengerAcademicoDatabase {
    public static final String NAME = "MessengerAcademicoDatabase"; // we will add the .db extension
    public static final int VERSION = 5;
}
