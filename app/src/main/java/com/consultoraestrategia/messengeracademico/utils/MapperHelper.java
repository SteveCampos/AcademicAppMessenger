package com.consultoraestrategia.messengeracademico.utils;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
