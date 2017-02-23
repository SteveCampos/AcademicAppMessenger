package com.consultoraestrategia.messengeracademico.verification;

import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseNumberVerification;
import com.consultoraestrategia.messengeracademico.entities.PhoneNumberVerified;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.lib.GreenRobotEventBus;
import com.consultoraestrategia.messengeracademico.verification.events.VerificationEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Steve on 17/02/2017.
 */
public class VerificationRepositoryImpl implements VerificationRepository {

    private static final String TAG = VerificationRepositoryImpl.class.getSimpleName();
    private FirebaseNumberVerification helper;

    public VerificationRepositoryImpl() {
        this.helper = new FirebaseNumberVerification();
    }

    @Override
    public void initVerificationProcess(final String phoneNumber) {
        helper.initVerifyPhoneNumber(phoneNumber, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    post(VerificationEvent.OnPhoneNumberSendedError, getPhoneNumberVerified(phoneNumber), databaseError.getMessage());
                } else {
                    post(VerificationEvent.OnPhoneNumberSended, getPhoneNumberVerified(phoneNumber), null);
                }
            }
        });
    }

    @Override
    public void validateCode(final String phoneNumber, final String code) {
        helper.validateCode(phoneNumber, code, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot: " + dataSnapshot  );
                if (dataSnapshot != null){
                    PhoneNumberVerified numberVerified = dataSnapshot.getValue(PhoneNumberVerified.class);
                    if (numberVerified != null){
                        if (numberVerified.getCode() != null  && numberVerified.getCode().equals(code)){
                            //Está validado el putazo!
                            post(VerificationEvent.OnPhoneNumberVerificated, numberVerified, null);
                            return;
                        }else{
                            post(VerificationEvent.OnInvalidCode, getPhoneNumberVerified(phoneNumber), "");
                            return;
                        }
                    }
                }

                post(VerificationEvent.OnPhoneNumberFailedToVerificated, getPhoneNumberVerified(phoneNumber), "Error: No se encontró el número.");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                post(VerificationEvent.OnPhoneNumberFailedToVerificated, getPhoneNumberVerified(phoneNumber), databaseError.getMessage());
            }
        });
    }

    private void post(int type, PhoneNumberVerified phoneNumberVerified, String errorMessage) {

        VerificationEvent event = new VerificationEvent();
        event.setType(type);
        event.setPhoneNumberVerified(phoneNumberVerified);
        if (errorMessage != null) {
            event.setError(errorMessage);
        }

        EventBus eventBus = GreenRobotEventBus.getInstance();
        eventBus.post(event);
    }

    private PhoneNumberVerified getPhoneNumberVerified(String phoneNumber){
        PhoneNumberVerified numberVerified = new PhoneNumberVerified();
        numberVerified.setPhoneNumber(phoneNumber);
        return numberVerified;
    }


}
