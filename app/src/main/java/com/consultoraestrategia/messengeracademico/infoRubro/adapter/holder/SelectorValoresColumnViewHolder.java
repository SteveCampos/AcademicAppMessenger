package com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.TextoColum;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SCIEV on 8/03/2018.
 */

public class SelectorValoresColumnViewHolder extends ColumnHeaderViewHolder<TextoColum>
{
    @BindView(R.id.viewFondo)
    TextView viewFondo;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.textNota)
    TextView textNota;
    @BindView(R.id.root)
    ConstraintLayout root;

    public SelectorValoresColumnViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bind(TextoColum rowHeader) {
        txtTitle.setText(rowHeader.getContenido());
        textNota.setText(rowHeader.getValorNumerico());
        switch (rowHeader.getColorNota()){
            case AZUL:
                viewFondo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.md_blue_700));
                txtTitle.setTextColor(Color.WHITE);
                textNota.setTextColor(Color.WHITE);
                break;
            case ROJO:
                viewFondo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.md_pink_100));
                txtTitle.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.md_red_700));
                textNota.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.md_red_700));
                break;
            case ANARANJADO:
                viewFondo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.md_orange_A700));
                txtTitle.setTextColor(Color.WHITE);
                textNota.setTextColor(Color.WHITE);
                break;
            case BLANCO:
                viewFondo.setBackgroundColor(Color.WHITE);
                break;
            case VERDE:
                viewFondo.setBackgroundColor(ContextCompat.getColor(itemView.getContext(),R.color.md_green_700));
                txtTitle.setTextColor(Color.WHITE);
                textNota.setTextColor(Color.WHITE);
                break;
        }
    }
}
