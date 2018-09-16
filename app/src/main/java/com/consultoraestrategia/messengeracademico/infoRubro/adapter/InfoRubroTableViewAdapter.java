package com.consultoraestrategia.messengeracademico.infoRubro.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.CornerHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.DefaultCellViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.DefaultColumnViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.EvaluacionCellViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.NotaCellViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.RubroRowViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.SelectorIconosRowViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.SelectorValoresColumnViewHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.CompetenciaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ImagenColumn;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.NotaCell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.TextoColum;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

/**
 * Created by Jse on 15/09/2018.
 */

public class InfoRubroTableViewAdapter extends AbstractTableAdapter<Column, Row, Cell> {

    private static final int CELL_COMPETENCIA = 1, CELL_NOTA = 2;
    private static final int COLUMN_TEXTO = 11, COLUMN_IMAGEN = 12;
    private CornerHolder viewHolder;

    public InfoRubroTableViewAdapter(Context p_jContext) {
        super(p_jContext);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        int cantidad = m_jCellItems.size();
        if (cantidad != 0) {
         Cell cell = m_jCellItems.get(0).get(position);
         if(cell instanceof CompetenciaCell){
             return CELL_COMPETENCIA;
         }else if(cell instanceof NotaCell){
             return CELL_NOTA;
         }
        }
            return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        if (m_jColumnHeaderItems.size() != 0) {
            Column column = m_jColumnHeaderItems.get(position);
           if(column instanceof TextoColum) {
               return COLUMN_TEXTO;
           } else if(column instanceof ImagenColumn){
               return COLUMN_IMAGEN;
           }
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout;
        switch (viewType) {
            default:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_empty, parent, false);
                return new DefaultCellViewHolder(layout);
            case CELL_COMPETENCIA:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cell_nota_competencia_layout, parent, false);
                return new NotaCellViewHolder(layout);
            case CELL_NOTA:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_cell_evaluacion_layout, parent, false);
                return new EvaluacionCellViewHolder(layout);
        }
    }

    @Override
    public void onBindCellViewHolder(AbstractViewHolder holder, Object p_jValue, int p_nXPosition, int p_nYPosition) {
        if (holder instanceof NotaCellViewHolder && p_jValue instanceof CompetenciaCell){
            CompetenciaCell competenciaCell = (CompetenciaCell)p_jValue;
            NotaCellViewHolder notaCellViewHolder = (NotaCellViewHolder)holder;
            notaCellViewHolder.bind(competenciaCell);
        }else if(holder instanceof EvaluacionCellViewHolder && p_jValue instanceof NotaCell){
            NotaCell notaCell = (NotaCell)p_jValue;
            EvaluacionCellViewHolder evaluacionCellViewHolder = (EvaluacionCellViewHolder)holder;
            evaluacionCellViewHolder.bind(notaCell);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_rubro_evaluacion_layout, parent, false);
        return new RubroRowViewHolder(layout);
    }

    @Override
    public void onBindColumnHeaderViewHolder(AbstractViewHolder holder, Object p_jValue, int p_nXPosition) {
        Row column =  (Row) p_jValue;
        RubroRowViewHolder rubroRowViewHolder = (RubroRowViewHolder)holder;
        rubroRowViewHolder.bind(column);
    }

    @Override
    public RecyclerView.ViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout;
        switch (viewType){
            default:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_empty, parent, false);
                return new DefaultColumnViewHolder(layout);
            case COLUMN_TEXTO:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_selector_valores_layout, parent, false);
                return new SelectorValoresColumnViewHolder(layout);
            case COLUMN_IMAGEN:
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_view_selector_iconos_layout, parent, false);
                return new SelectorIconosRowViewHolder(layout);
        }
    }

    @Override
    public void onBindRowHeaderViewHolder(AbstractViewHolder holder, Object p_jValue, int p_nYPosition) {
        if (holder instanceof SelectorValoresColumnViewHolder && p_jValue instanceof TextoColum) {
            TextoColum textoColum = (TextoColum)p_jValue;
            SelectorValoresColumnViewHolder selectorValoresColumnViewHolder = (SelectorValoresColumnViewHolder) holder;
            selectorValoresColumnViewHolder.bind(textoColum);
        }else if(holder instanceof SelectorIconosRowViewHolder && p_jValue instanceof ImagenColumn){
            ImagenColumn imagenColumn = (ImagenColumn)p_jValue;
            SelectorIconosRowViewHolder selectorIconosRowViewHolder = (SelectorIconosRowViewHolder)holder;
            selectorIconosRowViewHolder.bind(imagenColumn);
        }
    }

    @Override
    public View onCreateCornerView() {
        View view = LayoutInflater.from(m_jContext).inflate(R.layout.table_view_corner_layout_rubrica_individual, null);
        viewHolder = new CornerHolder(view);
        return view;
    }

    public CornerHolder getViewHolder() {
        return viewHolder;
    }
}
