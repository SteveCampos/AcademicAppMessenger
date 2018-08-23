package com.consultoraestrategia.messengeracademico.base;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.actionMode.SelectListener;
import com.consultoraestrategia.messengeracademico.base.actionMode.Selectable;

import butterknife.ButterKnife;

public abstract class SelectableViewHolder<T extends Selectable> extends RecyclerView.ViewHolder {
    protected abstract View getBgRegion();

    public SelectableViewHolder(View itemView) {
        super(itemView);
    }

    protected void bind(final T item, final SelectListener<T> listener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onClick(item, view);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (listener != null) listener.onLongClick(item, view);
                return false;
            }
        });


        int color = R.color.transparent;
        if (item.isSelected()) {
            color = R.color.colorPrimaryDarkOverlay;
        }
        int colorResolved = ContextCompat.getColor(itemView.getContext(), color);
        getBgRegion().setBackgroundColor(colorResolved);

    }
}
