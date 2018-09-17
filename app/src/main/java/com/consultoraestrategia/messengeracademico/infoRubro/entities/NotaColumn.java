package com.consultoraestrategia.messengeracademico.infoRubro.entities;

public class NotaColumn extends Column {
    private String contenido;

    @Override
    public String getContenido() {
        return contenido;
    }

    @Override
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
