package com.consultoraestrategia.messengeracademico.entities;

import android.support.annotation.Nullable;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

@Table(database = MessengerAcademicoDatabase.class)
public class GlobalSettings extends BaseModel {
    @PrimaryKey
    public int id;
    @Column
    public String nombre;
    @Column
    public String urlServer;

    public GlobalSettings() {
    }

    public GlobalSettings(int id, String nombre, String urlServer) {
        this.id = id;
        this.nombre = nombre;
        this.urlServer = urlServer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlServer() {
        return urlServer;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUrlServer(String urlServer) {
        this.urlServer = urlServer;
    }

    public @Nullable
    static GlobalSettings getCurrentSettings() {
        GlobalSettings globalSettings = null;
        try {
            globalSettings = SQLite.select()
                    .from(GlobalSettings.class)
                    .querySingle();

            if(globalSettings == null){
                globalSettings = init(Servers.PRODUCCION);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       return globalSettings;
    }

    private static GlobalSettings init(Servers servers) {
        GlobalSettings globalSettings = new GlobalSettings(1,servers.getNombre(),servers.getUrlServer());
        globalSettings.save();
        return globalSettings;
    }

    public static GlobalSettings getNewInstance(Servers servers) {
        return new GlobalSettings(1,servers.getNombre(),servers.getUrlServer());
    }

    public static String getServerUrl() {
        String serverUrl = null;
        GlobalSettings settings = getCurrentSettings();
        if (settings != null) {
            serverUrl = settings.getUrlServer();
        }
        return serverUrl;
    }


    public enum Servers{
        LOCAL("CRM Local",
                "http://192.168.1.151"),
        PRUEBAS("CRM Pruebas",
                "http://pruebas.consultoraestrategia.com"),
        PRODUCCION("CRM Producción",
                "http://crmeducativo.consultoraestrategia.com"),
        OTRO("Agregar Otro", "");

        private String nombre;
        private String urlServer;
        private final static String path = "/CRMMovil/PortalAcadMovil.ashx";

        Servers(String nombre, String urlServer) {
            this.nombre = nombre;
            this.urlServer = urlServer + path;
        }

        public String getNombre() {
            return nombre;
        }

        public String getUrlServer() {
            return urlServer;
        }

        public String getPath() {
            return path;
        }
    }

}
