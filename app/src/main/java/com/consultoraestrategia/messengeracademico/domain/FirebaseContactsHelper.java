package com.consultoraestrategia.messengeracademico.domain;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USERS;


/**
 * Created by @stevecampos on 3/03/2017.
 */

public class FirebaseContactsHelper extends FirebaseHelper {


    private static final String CHILD_PHONENUMBERS = "phoneNumbers";


    private static final String PATH_PHONENUMBERS = "/" + CHILD_PHONENUMBERS + "/";
    private static final String TAG = FirebaseContactsHelper.class.getSimpleName();

    private DatabaseReference phoneNumbersRef;

    public FirebaseContactsHelper() {
        super();
        phoneNumbersRef = getDatabase().getReference(PATH_PHONENUMBERS);
    }

    public void existPhoneNumber(String phoneNumber, ValueEventListener listener) {
        phoneNumbersRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }

    public void removeListener(String phoneNumber, ValueEventListener listener) {
        phoneNumbersRef.child(phoneNumber).removeEventListener(listener);
    }

    public void postPhoneNumber(String phoneNumber, DatabaseReference.CompletionListener listener) {
        /*String phoneKey = phoneToCodeRef.push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put(phoneNumber, phoneKey);
        phoneToCodeRef.updateChildren(map, listener);*/
    }

    public void listenUserProfile(String uid, ValueEventListener listener) {
        getDatabase()
                .getReference()
                .child(CHILD_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(listener);
    }

    public void getUserKey(String phoneNumber, ValueEventListener listener) {
        Log.e(TAG, "DEPRECATED!!!");
        //phoneToCodeRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }
}