package com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder;

import android.view.View;

import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

/**
 * Created by SCIEV on 8/03/2018.
 */

public abstract class ColumnHeaderViewHolder<T extends Column> extends AbstractViewHolder {
    public ColumnHeaderViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T rowHeader);
}
