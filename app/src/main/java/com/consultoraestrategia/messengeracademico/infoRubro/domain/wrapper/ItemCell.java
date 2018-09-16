package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class ItemCell {
    private String content;
    private String urlImg;

    public ItemCell(String content, String urlImg) {
        this.content = content;
        this.urlImg = urlImg;
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
}
