package com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.NotaColumn;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotaRowViewHolder extends ColumnHeaderViewHolder<NotaColumn>
{
    @BindView(R.id.txt_nota_evaluacion)
    TextView txtNotaEvaluacion;
    @BindView(R.id.root)
    ConstraintLayout root;
    public NotaRowViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void bind(NotaColumn rowHeader) {
        txtNotaEvaluacion.setText(rowHeader.getContenido());
    }
}

