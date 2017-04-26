package com.consultoraestrategia.messengeracademico.domain;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_PROFILE;
import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USER;

/**
 * Created by @stevecampos on 3/03/2017.
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

    public void listenUserProfile(String userKey, ValueEventListener listener) {
        getDatabase()
                .getReference()
                .child(CHILD_USER)
                .child(userKey)
                .child(CHILD_PROFILE)
                .addListenerForSingleValueEvent(listener);
    }

    public void getUserKey(final String phoneNumber, ValueEventListener listener) {
        phoneToCodeRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }
}