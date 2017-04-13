package com.consultoraestrategia.messengeracademico.entities;

import java.util.HashMap;

/**
 * Created by kike on 11/04/2017.
 */

public class Photo {
    private String url;
    private String height;
    private String width;

    public Photo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public HashMap<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("url",url);
        return result;
    }
}
