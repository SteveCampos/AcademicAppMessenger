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
public class Relacion extends BaseModel {
    @PrimaryKey
    @Column
    private int relacionId;
    @Column
    private int tipoId;
    @Column
    private int personaPrincipalId;
    @Column
    private int personaVinculadaId;

    public Relacion() {
    }
    public int getRelacionId() {
        return relacionId;
    }

    public void setRelacionId(int relacionId) {
        this.relacionId = relacionId;
    }

    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
    }

    public int getPersonaPrincipalId() {
        return personaPrincipalId;
    }

    public void setPersonaPrincipalId(int personaPrincipalId) {
        this.personaPrincipalId = personaPrincipalId;
    }

    public int getPersonaVinculadaId() {
        return personaVinculadaId;
    }

    public void setPersonaVinculadaId(int personaVinculadaId) {
        this.personaVinculadaId = personaVinculadaId;
    }
}
