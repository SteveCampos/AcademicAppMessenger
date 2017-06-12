package com.consultoraestrategia.messengeracademico.utils;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Created by jairc on 19/04/2017.
 */

public class MapperHelper {

    private static final String TAG = "MapperHelper";

    public <T> T mapToObject(Map<String, String> map, Class<T> cls) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(map, cls);
        } catch (Exception ex) {
            Log.d(TAG, "Exception: " + ex);
            return null;
        }
    }

    public static String ObjectToString(Object object) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T StringToObject(String json, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
