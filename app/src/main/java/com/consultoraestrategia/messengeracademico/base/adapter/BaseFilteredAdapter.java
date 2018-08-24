package com.consultoraestrategia.messengeracademico.base.adapter;

import android.support.v7.widget.RecyclerView;

import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;

import java.util.List;

public class BaseFilteredAdapter<T extends Object, VH extends RecyclerView.ViewHolder> extends BaseAdapter<T, VH> {
    private List<T> listFiltered;


    public BaseFilteredAdapter(List<T> items) {
        super(items);
    }
}
