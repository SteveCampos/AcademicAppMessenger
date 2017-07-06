package com.consultoraestrategia.messengeracademico.entities;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by kike on 22/06/2017.
 */
@Table(database = MessengerAcademicoDatabase.class)
public class NivelesAcademicos extends BaseModel {
    @PrimaryKey
    @Column
    private int nivelAcademicoId;
    @Column
    private String alias;
    @Column
    private String nombre;
    @Column
    private int entidadId;

    public NivelesAcademicos() {

    }

    public int getNivelAcademicoId() {
        return nivelAcademicoId;
    }

    public void setNivelAcademicoId(int nivelAcademicoId) {
        this.nivelAcademicoId = nivelAcademicoId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(int entidadId) {
        this.entidadId = entidadId;
    }
}
