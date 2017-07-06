package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kike on 14/06/2017.
 */
@Table(database = MessengerAcademicoDatabase.class)
public class GeorefRolPersona extends BaseModel {
    @PrimaryKey(autoincrement =  true)
    @Column
    private int georefRolPersonaId;
    @Column
    private int personaId;
    @Column
    private int georeferenciaId;
    @Column
    private int rolId;
    @Column
    private int entidadId;

    public GeorefRolPersona() {

    }

    public int getGeorefRolPersonaId() {
        return georefRolPersonaId;
    }

    public void setGeorefRolPersonaId(int georefRolPersonaId) {
        this.georefRolPersonaId = georefRolPersonaId;
    }

    public int getPersonaId() {
        return personaId;
    }

    public void setPersonaId(int personaId) {
        this.personaId = personaId;
    }

    public int getGeoreferenciaId() {
        return georeferenciaId;
    }

    public void setGeoreferenciaId(int georeferenciaId) {
        this.georeferenciaId = georeferenciaId;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public int getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(int entidadId) {
        this.entidadId = entidadId;
    }
}
