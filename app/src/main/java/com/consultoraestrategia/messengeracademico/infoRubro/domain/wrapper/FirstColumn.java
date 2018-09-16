package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class FirstColumn {
    String nombreComp;

    public FirstColumn(String nombreComp) {
        this.nombreComp = nombreComp;
    }

    public String getNombreComp() {
        return nombreComp;
    }

    public void setNombreComp(String nombreComp) {
        this.nombreComp = nombreComp;
    }

    @Override
    public String toString() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", getNombreComp());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }


    }
}
