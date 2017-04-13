package com.consultoraestrategia.messengeracademico.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 9/03/2017.
 */
public class Connection {
    private boolean online;
    private long timestamp;

    public Connection() {
    }

    public Connection(boolean online, long timestamp) {
        this.online = online;
        this.timestamp = timestamp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("online", online);
        result.put("timestamp", timestamp);
        return result;
    }
}
