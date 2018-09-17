package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import java.util.List;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class RubroUIFIrebase {
    private static final String TAG = RubroUIFIrebase.class.getSimpleName();
    private String nombreCursoGradoSeccion;
    private String nombreRubrica;
    private String nombre;
    private String apellido;
    private String urlImg;
    private String puntos;
    private String nota;
    private String desempenio;
    private String logro;
    private List<FirstColumn> columna;
    private List<HeaderTable> fila;
    private List<ListCells> cells;

    public RubroUIFIrebase() {
    }

    public String getNombreCursoGradoSeccion() {
        return nombreCursoGradoSeccion;
    }

    public void setNombreCursoGradoSeccion(String nombreCursoGradoSeccion) {
        this.nombreCursoGradoSeccion = nombreCursoGradoSeccion;
    }

    public String getNombreRubrica() {
        return nombreRubrica;
    }

    public void setNombreRubrica(String nombreRubrica) {
        this.nombreRubrica = nombreRubrica;
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

    public String getDesempenio() {
        return desempenio;
    }

    public void setDesempenio(String desempenio) {
        this.desempenio = desempenio;
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
}
