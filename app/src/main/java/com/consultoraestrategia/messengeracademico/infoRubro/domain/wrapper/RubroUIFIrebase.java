package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import java.util.List;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class RubroUIFIrebase {
    private static final String TAG = RubroUIFIrebase.class.getSimpleName();
    private String nombre;
    private String apellido;
    private String urlImg;
    private String puntos;
    private String nota;
    private String desempeño;
    private String logro;
    private List<FirstColumn> columna;
    private List<HeaderTable> fila;
    private List<ListCells> cells;


    public RubroUIFIrebase(String nombre, String apellido, String urlImg, String puntos, String nota, String desempeño, String logro, List<FirstColumn> columna, List<HeaderTable> fila, List<ListCells> cells) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.urlImg = urlImg;
        this.puntos = puntos;
        this.nota = nota;
        this.desempeño = desempeño;
        this.logro = logro;
        this.columna = columna;
        this.fila = fila;
        this.cells = cells;
    }

    public RubroUIFIrebase() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getPuntos() {
        return puntos;
    }

    public void setPuntos(String puntos) {
        this.puntos = puntos;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getDesempeño() {
        return desempeño;
    }

    public void setDesempeño(String desempeño) {
        this.desempeño = desempeño;
    }

    public String getLogro() {
        return logro;
    }

    public void setLogro(String logro) {
        this.logro = logro;
    }

    public List<FirstColumn> getColumna() {
        return columna;
    }

    public void setColumna(List<FirstColumn> columna) {
        this.columna = columna;
    }

    public List<HeaderTable> getFila() {
        return fila;
    }

    public void setFila(List<HeaderTable> fila) {
        this.fila = fila;
    }

    public List<ListCells> getCells() {
        return cells;
    }

    public void setCells(List<ListCells> cells) {
        this.cells = cells;
    }

    @Override
    public String toString() {
        return "RubroUIFIrebase{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", urlImg='" + urlImg + '\'' +
                ", puntos='" + puntos + '\'' +
                ", nota='" + nota + '\'' +
                ", desempeño='" + desempeño + '\'' +
                ", logro='" + logro + '\'' +
                ", columna=" + columna.size() +
                ", fila=" + fila.size() +
                ", cells=" + cells.size() +
                '}';
    }
}
