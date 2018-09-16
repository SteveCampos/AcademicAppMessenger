package com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder;

import android.view.View;

import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

/**
 * Created by SCIEV on 8/03/2018.
 */

public abstract class CellViewHolder<T extends Cell> extends AbstractViewHolder {

    public CellViewHolder(View itemView) {
        super(itemView);
    }
    public abstract void bind(T cell);

}
