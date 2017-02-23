package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.PhoneNumberVerified;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 17/02/2017.
 */

public class FirebaseNumberVerification extends FirebaseHelper{

    public static final String CHILD_VERIFIED_NUMBERS = "VERIFIED_NUMBERS";
    public static final String CHILD_ALL = "ALL";
    public static final String CHILD_IN_PROCESS = "IN_PROCESS";

    public static final String PATH_IN_PROCESS = "/" + CHILD_VERIFIED_NUMBERS + "/" + CHILD_IN_PROCESS + "/";
    public static final String PATH_ALL = "/" + CHILD_VERIFIED_NUMBERS + "/" + CHILD_ALL + "/";
    private static final String TAG = FirebaseNumberVerification.class.getSimpleName();

    private DatabaseReference verifiedNumbersRef;

    public FirebaseNumberVerification() {
        super();
        this.verifiedNumbersRef = getDatabase().getReference(CHILD_VERIFIED_NUMBERS);
    }

    public void initVerifyPhoneNumber(String phoneNumber, DatabaseReference.CompletionListener listener){
        Map<String, Object> map = new HashMap<>();
        
        PhoneNumberVerified phoneNumberVerified = new PhoneNumberVerified();
        phoneNumberVerified.setPhoneNumber(phoneNumber);
        phoneNumberVerified.setState(PhoneNumberVerified.PHONE_NUMBER_SENDED_TO_SERVER);
        phoneNumberVerified.setInitTime(new Date().getTime());
        phoneNumberVerified.setFinishTime(new Date().getTime());

        map.put(PATH_IN_PROCESS + phoneNumber, phoneNumberVerified.toMap());
        map.put(PATH_ALL + phoneNumber, phoneNumberVerified.toMap());

        getDatabase().getReference().updateChildren(map, listener);
    }

    public void validateCode(String phoneNumber, final String code, ValueEventListener listener){
        verifiedNumbersRef.child(CHILD_ALL).child(phoneNumber).addListenerForSingleValueEvent(listener);
    }
}
