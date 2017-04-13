package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 3/03/2017.
 */

public class FirebaseContactsHelper extends FirebaseHelper {


    private static final String CHILD_MAP = "MAP";
    private static final String CHILD_PHONE_TO_CODE = "PHONE_TO_CODE";
    private static final String CHILD_CODE_TO_PHONE = "CODE_TO_PHONE";
    private static final String CHILD_ALL = "ALL";


    private static final String PATH_PHONE_TO_CODE = "/" + CHILD_MAP + "/" + CHILD_PHONE_TO_CODE + "/" + CHILD_ALL + "/";
    private static final String TAG = FirebaseContactsHelper.class.getSimpleName();

    private DatabaseReference phoneToCodeRef;

    public FirebaseContactsHelper() {
        super();
        phoneToCodeRef = getDatabase().getReference(PATH_PHONE_TO_CODE);
    }

    public void existPhoneNumber(String phoneNumber, ValueEventListener listener) {
        phoneToCodeRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }

    public void removeListener(String phoneNumber, ValueEventListener listener) {
        phoneToCodeRef.child(phoneNumber).removeEventListener(listener);
    }

    public void postPhoneNumber(String phoneNumber, DatabaseReference.CompletionListener listener) {
        String phoneKey = phoneToCodeRef.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put(phoneNumber, phoneKey);
        phoneToCodeRef.updateChildren(map, listener);
    }

    public String getUserKey(final String phoneNumber, ValueEventListener listener) {
        phoneToCodeRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
        return null;
    }
}