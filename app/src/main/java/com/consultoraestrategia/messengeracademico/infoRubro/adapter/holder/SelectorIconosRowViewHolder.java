package com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder;

import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.ImagenColumn;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SCIEV on 8/03/2018.
 */

public class SelectorIconosRowViewHolder extends ColumnHeaderViewHolder<ImagenColumn>
{
    @BindView(R.id.imgResultado)
    CircleImageView imgResultado;
    @BindView(R.id.root)
    ConstraintLayout root;
    public SelectorIconosRowViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @Override
    public void bind(ImagenColumn rowHeader) {
        Glide.with(itemView.getContext())
                .load(rowHeader.getContenido())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_error_outline_black)
                .into(imgResultado);
    }
}
