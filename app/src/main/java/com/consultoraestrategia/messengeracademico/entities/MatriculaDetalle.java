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
public class MatriculaDetalle extends BaseModel{
    @PrimaryKey
    @Column
    private int matriculaDetalleId;
    @Column
    private int cargaCursoId;
    @Column
    private int matriculaid;
    @Column
    private int cursoId;

    public MatriculaDetalle() {
    }
    public int getMatriculaDetalleId() {
        return matriculaDetalleId;
    }

    public void setMatriculaDetalleId(int matriculaDetalleId) {
        this.matriculaDetalleId = matriculaDetalleId;
    }

    public int getCargaCursoId() {
        return cargaCursoId;
    }

    public void setCargaCursoId(int cargaCursoId) {
        this.cargaCursoId = cargaCursoId;
    }

    public int getMatriculaid() {
        return matriculaid;
    }

    public void setMatriculaid(int matriculaid) {
        this.matriculaid = matriculaid;
    }

    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }
}
