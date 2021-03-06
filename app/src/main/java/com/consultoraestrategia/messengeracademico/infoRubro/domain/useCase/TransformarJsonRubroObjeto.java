package com.consultoraestrategia.messengeracademico.infoRubro.domain.useCase;

import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.FirstColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.HeaderTable;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.ItemCell;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.ListCells;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.RubroUIFIrebase;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Alumno;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ColorNota;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.CompetenciaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ImagenColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.NotaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.NotaColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.TextoColum;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jse on 16/09/2018.
 */

public class TransformarJsonRubroObjeto {
    public static final int COMPETENCIA_BASE = 347;
    public static final int COMPETENCIA_TRANS = 348;
    public static final int COMPETENCIA_ENFQ = 349;
    public interface Callback{
        void onSuccess(Response response);
        void onError();
    }
    public void excute(Request request, Callback callback){
        try {
            Gson gson = new Gson();
            RubroUIFIrebase rubroUIFIrebase = gson.fromJson(request.getJson(), RubroUIFIrebase.class);

            List<Row> rowList = new ArrayList<>();
            for (FirstColumn firstColumn: rubroUIFIrebase.getColumna()){
                Row row = new Row();
                row.setContenido(firstColumn.getNombreComp());
                rowList.add(row);
            }

            List<Column> columnList = new ArrayList<>();
            int count = 0;
            for (HeaderTable headerTable: rubroUIFIrebase.getFila()){
                if(count == 0){
                    NotaColumn notaColumn = new NotaColumn();
                    notaColumn.setContenido(headerTable.getContent());
                    columnList.add(notaColumn);
                }else if(!TextUtils.isEmpty(headerTable.getImg())){
                    Log.d(TransformarJsonRubroObjeto.class.getSimpleName(), "img:"+headerTable.getImg());
                    Log.d(TransformarJsonRubroObjeto.class.getSimpleName(), "conten:"+headerTable.getContent());
                    ImagenColumn imagenColumn = new ImagenColumn();
                    imagenColumn.setContenido(headerTable.getImg());
                    columnList.add(imagenColumn);
                }else {
                    TextoColum textoColum = new TextoColum();
                    textoColum.setContenido(headerTable.getContent());
                    switch (count){
                        default:
                            textoColum.setColorNota(ColorNota.BLANCO);
                            break;
                        case 1:
                            textoColum.setColorNota(ColorNota.AZUL);
                            break;
                        case 2:
                            textoColum.setColorNota(ColorNota.VERDE);
                            break;
                        case 3:
                            textoColum.setColorNota(ColorNota.ANARANJADO);
                            break;
                        case 4:
                            textoColum.setColorNota(ColorNota.ROJO);
                            break;
                    }
                    columnList.add(textoColum);
                }
                count ++;
            }

            List<List<Cell>> cellListList = new ArrayList<>();
            for (ListCells listCells :rubroUIFIrebase.getCells()){
                List<Cell> cellList = new ArrayList<>();
                for(int i = 0; i< listCells.getItemCellList().size(); i++){
                    ItemCell itemCell = listCells.getItemCellList().get(i);
                    if(i == 0){
                        CompetenciaCell competenciaCell = new CompetenciaCell();
                        competenciaCell.setContenido(itemCell.getContent());
                        switch (itemCell.getCompetenciaId()){
                            case COMPETENCIA_BASE:
                                competenciaCell.setTipo(CompetenciaCell.Tipo.BASE);
                                break;
                            case COMPETENCIA_ENFQ:
                                competenciaCell.setTipo(CompetenciaCell.Tipo.ENFOQUE);
                                break;
                            case COMPETENCIA_TRANS:
                                competenciaCell.setTipo(CompetenciaCell.Tipo.TRANSVERSAL);
                                break;
                                default:
                                    competenciaCell.setTipo(CompetenciaCell.Tipo.BASE);
                                break;
                        }
                        cellList.add(competenciaCell);
                    }else {
                        NotaCell notaCell = new NotaCell();
                        notaCell.setSelected(itemCell.isSelect());
                        switch (i){
                            default:
                                notaCell.setColor(ColorNota.BLANCO);
                                break;
                            case 1:
                                notaCell.setColor(ColorNota.AZUL);
                                break;
                            case 2:
                                notaCell.setColor(ColorNota.VERDE);
                                break;
                            case 3:
                                notaCell.setColor(ColorNota.ANARANJADO);
                                break;
                            case 4:
                                notaCell.setColor(ColorNota.ROJO);
                                break;
                        }
                        cellList.add(notaCell);
                    }
                }
                cellListList.add(cellList);
            }
            Alumno alumno = new Alumno();
            alumno.setNombre(rubroUIFIrebase.getNombre());
            alumno.setApellido(rubroUIFIrebase.getApellido());
            alumno.setDesempenio(rubroUIFIrebase.getDesempenio());
            alumno.setLogro(rubroUIFIrebase.getLogro());
            alumno.setPuntos(rubroUIFIrebase.getPuntos());
            alumno.setNota(rubroUIFIrebase.getNota());
            alumno.setUrlImg(rubroUIFIrebase.getUrlImg());
            callback.onSuccess(new Response(cellListList ,columnList , rowList ,alumno, rubroUIFIrebase.getNombreRubrica(), rubroUIFIrebase.getNombreCursoGradoSeccion()));

        }catch (Exception e){
            e.printStackTrace();
            callback.onError();
            Log.d(TransformarJsonRubroObjeto.class.getSimpleName(), "Error");
        }
    }

    public static class Request{
        private String json;

        public Request(String json) {
            this.json = json;
        }

        public String getJson() {
            return json;
        }
    }

    public static class Response{
        private List<List<Cell>> cellListList;
        private List<Column> columnList;
        private List<Row> rowList;
        private Alumno alumno;
        private String nombreRubrica;
        private String nombreCurso;

        public Response(List<List<Cell>> cellListList, List<Column> columnList, List<Row> rowList, Alumno alumno, String nombreRubrica, String nombreCurso) {
            this.cellListList = cellListList;
            this.columnList = columnList;
            this.rowList = rowList;
            this.alumno = alumno;
            this.nombreRubrica = nombreRubrica;
            this.nombreCurso = nombreCurso;
        }

        public void setCellListList(List<List<Cell>> cellListList) {
            this.cellListList = cellListList;
        }

        public void setColumnList(List<Column> columnList) {
            this.columnList = columnList;
        }

        public void setRowList(List<Row> rowList) {
            this.rowList = rowList;
        }

        public Alumno getAlumno() {
            return alumno;
        }

        public void setAlumno(Alumno alumno) {
            this.alumno = alumno;
        }

        public List<List<Cell>> getCellListList() {
            return cellListList;
        }

        public List<Column> getColumnList() {
            return columnList;
        }

        public List<Row> getRowList() {
            return rowList;
        }

        public String getNombreRubrica() {
            return nombreRubrica;
        }

        public String getNombreCurso() {
            return nombreCurso;
        }
    }
}
