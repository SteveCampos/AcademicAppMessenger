package com.consultoraestrategia.messengeracademico.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 17/02/2017.
 */

public class PhoneNumberVerified extends PhoneNumber {

    public static final String PHONE_NUMBER_SENDED_TO_SERVER = "PHONE_NUMBER_SENDED";
    public static final String PHONE_NUMBER_SERVER_SMS_SENDED = "PHONE_NUMBER_SERVER_SMS_SENDED";
    public static final String PHONE_NUMBER_SMS_RECEIVED = "PHONE_NUMBER_SMS_RECEIVED";
    public static final String PHONE_NUMBER_VERIFIED = "PHONE_NUMBER_VERIFIED";

    private String code;
    private String state;
    private long initTime;
    private long finishTime;


    public PhoneNumberVerified() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("state", state);
        result.put("initTime", initTime);
        result.put("finishTime", finishTime);
        result.put("phoneNumber", phoneNumber);
        result.put("code", "123456");
        return result;
    }

}
