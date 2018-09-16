package com.consultoraestrategia.messengeracademico.infoRubro.domain.useCase;

import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.FirstColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.HeaderTable;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.ItemCell;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.ListCells;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.RubroUIFIrebase;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ColorNota;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.CompetenciaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ImagenColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.NotaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.TextoColum;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jse on 16/09/2018.
 */

public class TransformarJsonRubroObjeto {
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
                if(!TextUtils.isEmpty(headerTable.getImg())){
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
                        case 0:
                            textoColum.setColorNota(ColorNota.AZUL);
                            break;
                        case 1:
                            textoColum.setColorNota(ColorNota.VERDE);
                            break;
                        case 2:
                            textoColum.setColorNota(ColorNota.ANARANJADO);
                            break;
                        case 3:
                            textoColum.setColorNota(ColorNota.ROJO);
                            break;
                    }
                    columnList.add(textoColum);
                }
                count ++;
            }

            List<List<Cell>> cellListList = new ArrayList<>();
            for (int i = 0; i< rubroUIFIrebase.getCells().size(); i++){
                List<Cell> cellList = new ArrayList<>();
                ListCells listCells = rubroUIFIrebase.getCells().get(i);
                for (ItemCell itemCell: listCells.getItemCellList()){
                    if(i == 0){
                        CompetenciaCell competenciaCell = new CompetenciaCell();
                        competenciaCell.setContenido(itemCell.getContent());
                        competenciaCell.setTipo(CompetenciaCell.Tipo.ENFOQUE);
                        cellList.add(competenciaCell);
                    }else {
                        NotaCell notaCell = new NotaCell();
                        if(!TextUtils.isEmpty(itemCell.getContent())){
                            notaCell.setSelected(true);
                        }
                        cellList.add(notaCell);
                    }
                }
                cellListList.add(cellList);
            }

            callback.onSuccess(new Response(cellListList,columnList, rowList));
            Log.d(TransformarJsonRubroObjeto.class.getSimpleName(), "Joson: " + request.getJson());
            Log.d(TransformarJsonRubroObjeto.class.getSimpleName(), rubroUIFIrebase.toString());

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

        public Response(List<List<Cell>> cellListList, List<Column> columnList, List<Row> rowList) {
            this.cellListList = cellListList;
            this.columnList = columnList;
            this.rowList = rowList;
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
    }
}
