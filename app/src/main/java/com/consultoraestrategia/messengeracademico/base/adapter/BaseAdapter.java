package com.consultoraestrategia.messengeracademico.base.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public interface Listener<T> {
        void onItemSelected(T item);
        void onItemLongSelected(T item);
    }

    protected List<T> items = new ArrayList<>();
    protected Listener<T> listener;

    public BaseAdapter(List<T> items) {
        if (items != null && !items.isEmpty()) {
            this.items = items;
        }
    }

    public void setListener(Listener<T> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addOrUpdate(T item) {
        int indexOf = items.indexOf(item);
        if (indexOf == -1) {
            addItem(item);
        } else {
            updateItem(item);
        }
    }

    private void addItem(T item) {
        items.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    private void updateItem(T item) {
        int position = items.indexOf(item);
        if (position == -1) return;

        items.set(position, item);
        notifyItemChanged(position);
    }

    public void removeItem(T item) {
        int position = items.indexOf(item);
        if (position == -1) return;


        items.remove(position);
        notifyItemRemoved(position);
    }


    public void addItems(List<T> items) {
        //Creo que aquí pueda dar un ANR!
        /*int countAntes = getItemCount();
        this.items.addAll(items);
        int countDespuesDeInsertar = getItemCount();
        notifyItemRangeInserted(countAntes - 1, countDespuesDeInsertar - 1);*/
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public void setItems(List<T> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public List<T> getItems() {
        return items;
    }
}
