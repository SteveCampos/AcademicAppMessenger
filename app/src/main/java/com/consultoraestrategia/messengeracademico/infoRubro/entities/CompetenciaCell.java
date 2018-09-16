package com.consultoraestrategia.messengeracademico.infoRubro.entities;

/**
 * Created by Jse on 15/09/2018.
 */

public class CompetenciaCell extends Cell{
    public enum Tipo{TRANSVERSAL, ENFOQUE,BASE}
    private Tipo tipo;

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
