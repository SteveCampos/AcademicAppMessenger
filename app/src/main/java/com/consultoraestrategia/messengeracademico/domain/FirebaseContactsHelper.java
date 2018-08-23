package com.consultoraestrategia.messengeracademico.domain;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USERS;


/**
 * Created by @stevecampos on 3/03/2017.
 */

public class FirebaseContactsHelper extends FirebaseHelper {


    private static final String CHILD_PHONENUMBERS = "phoneNumbers";
    private static final String PATH_PHONENUMBERS = "/" + CHILD_PHONENUMBERS + "/";
    public static final String PATH_USER_CONTACTS = "/user_contacts/";
    private static final String TAG = FirebaseContactsHelper.class.getSimpleName();

    private DatabaseReference phoneNumbersRef;

    private FirebaseContactsHelper() {
        super();
        phoneNumbersRef = getDatabase().getReference(PATH_PHONENUMBERS);
    }

    public static FirebaseContactsHelper getInstance() {
        return new FirebaseContactsHelper();
    }

    public void existPhoneNumber(String phoneNumber, ValueEventListener listener) {
        phoneNumbersRef.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }

    public void removeListener(String phoneNumber, ValueEventListener listener) {
        phoneNumbersRef.child(phoneNumber).removeEventListener(listener);
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

    public void saveContacsFromUser(String userUid, List<Contact> contactList, final Listener listener) {
        Map<String, Object> contactsMap = new HashMap<>();
        for (Contact contact :
                contactList) {
            String phoneNumber = contact.getPhoneNumber();
            String phoneNumberCleaned = StringUtils.removeInvalidCharacterFirebase(phoneNumber);
            if (!TextUtils.isEmpty(phoneNumberCleaned)) {
                contactsMap.put(PATH_USER_CONTACTS + userUid + "/" + phoneNumberCleaned, contact.toUserContactMap());
            }
        }
        getDatabase().getReference().updateChildren(contactsMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: ");
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    }
                });
    }
}