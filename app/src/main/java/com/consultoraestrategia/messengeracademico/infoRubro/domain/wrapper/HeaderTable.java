package com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by irvinmarin on 14/09/2018.
 */

public class HeaderTable {
    private String content;
    private String img;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public HeaderTable(String content, String img) {
        this.content = content;
        this.img = img;
    }


    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", getContent());
            jsonObject.put("img", getImg());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}

