package com.consultoraestrategia.messengeracademico.base.actionMode;

import android.content.res.Resources;
import android.support.annotation.MenuRes;
import android.util.Log;
import android.view.View;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.lib.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class BasePresenterActionModeImpl<V extends BaseActionModeView, T extends Selectable> extends BasePresenterImpl<V> implements BasePresenterActionMode<V, T> {


    protected abstract void updateItem(T item);

    protected abstract @MenuRes
    int getMenuActionMode();

    public BasePresenterActionModeImpl(UseCaseHandler handler, Resources res, EventBus eventBus) {
        super(handler, res, eventBus);
    }

    private boolean inSelectedMode = false;
    private Map<String, T> selectedItems = new LinkedHashMap<>();

    public boolean isInSelectedMode() {
        return inSelectedMode;
    }

    @Override
    public void onClick(T data, View view) {
        Log.d(getTag(), "onClick: ");

        if (getSelectedSize() >= 1) {
            boolean containsKey = selectedItems.containsKey(data.getKey());
            if (containsKey) {
                changeItemToNotSelected(data);
            } else {
                addItemToSelectedMode(data);
            }
        }

    }

    private int getSelectedSize() {
        return selectedItems.size();
    }

    @Override
    public void onLongClick(T data, View view) {
        Log.d(getTag(), "onLongClick: ");
        if (!inSelectedMode) {
            inSelectedMode = true;
            startActionMode();
            addItemToSelectedMode(data);
        }
    }

    private void startActionMode() {
        if (view != null) {
            view.createActionMode(getMenuActionMode());
        }
    }

    private void setActionModeTitle(String title) {
        if (view != null) {
            view.setActionModeTitle(title);
        }
    }

    private void destroyActionMode() {
        if (view != null) view.destroyActionMode();
    }

    protected void addItemToSelectedMode(T message) {
        selectedItems.put(message.getKey(), message);
        message.setSelected(true);
        updateActionModeTitle();
        updateItem(message);
    }

    protected void removeItemFromSelectedMode(T item) {
        selectedItems.remove(item.getKey());
        item.setSelected(false);

        if (selectedItems.size() == 0) {
            inSelectedMode = false;
            destroyActionMode();
            return;
        }

        updateActionModeTitle();
    }

    protected void clearItems() {
        List<T> selectedItems = getItemsSelected();
        for (T t :
                selectedItems) {
            changeItemToNotSelected(t);
        }
    }

    protected void changeItemToNotSelected(T message) {
        selectedItems.remove(message.getKey());
        message.setSelected(false);

        updateItem(message);
        if (selectedItems.size() == 0) {
            inSelectedMode = false;
            destroyActionMode();
            return;
        }

        updateActionModeTitle();
    }

    private void updateActionModeTitle() {
        if (getSelectedSize() > 0) {
            setActionModeTitle(String.valueOf(getSelectedSize()));
        }
    }

    protected List<T> getItemsSelected() {
        List<T> itemsSelected = new ArrayList<>();
        for (Map.Entry<String, T> entry : selectedItems.entrySet()) {
            itemsSelected.add(entry.getValue());
        }
        return itemsSelected;
    }
    @Override
    public void onDestroyActionMode(){
        Log.d(getTag(), "onDestroyActionMode: ");
        clearItems();
    }

}
