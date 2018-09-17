package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class ItemCell {
    private int competenciaId;
    private String content;
    private String urlImg;
    private boolean select;


    public ItemCell(int competenciaId, String content, String urlImg, boolean select) {
        this.competenciaId = competenciaId;
        this.content = content;
        this.urlImg = urlImg;
        this.select = select;
    }

    public int getCompetenciaId() {
        return competenciaId;
    }

    public void setCompetenciaId(int competenciaId) {
        this.competenciaId = competenciaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", getContent());
            jsonObject.put("urlImg", getUrlImg());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
