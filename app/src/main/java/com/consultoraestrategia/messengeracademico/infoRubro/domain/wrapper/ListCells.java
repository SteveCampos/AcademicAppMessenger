package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class ListCells {
    private List<ItemCell> itemCellList;


    public ListCells() {
    }

    public List<ItemCell> getItemCellList() {
        return itemCellList;
    }

    public void setItemCellList(List<ItemCell> itemCellList) {
        this.itemCellList = itemCellList;
    }

    public ListCells(List<ItemCell> itemCellList) {
        this.itemCellList = itemCellList;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("itemCellList", getItemCellList());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

}
