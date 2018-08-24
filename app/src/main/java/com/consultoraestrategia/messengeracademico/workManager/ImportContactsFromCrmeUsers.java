package com.consultoraestrategia.messengeracademico.workManager;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.domain.FirebaseContactsHelper;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.domain.FirebaseUser;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser_Table;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.work.Worker;

public class ImportContactsFromCrmeUsers extends Worker {
    private static final String TAG = "ImportContactsFromCrme";
    private int totalCrmeUser = 0;
    private int noTienenMessenger = 0;
    private int siTienenMessenger = 0;
    private int contactosObtenidos = 0;
    private int contactosFallidosDeObtener = 0;

    CountDownLatch countDownLatch;

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: ");
        List<CrmeUser> crmeUserList =
                SQLite.select()
                        .from(CrmeUser.class)
                        .where(CrmeUser_Table.phoneNumber.notEq(""))
                        .queryList();
        countDownLatch = new CountDownLatch(1);
        totalCrmeUser = crmeUserList.size();

        if (totalCrmeUser <= 0) {
            return Result.SUCCESS;
        }

        FirebaseContactsHelper contactsHelper = FirebaseContactsHelper.getInstance();
        final FirebaseUser userHelper = FirebaseUser.getInstance();

        for (CrmeUser crmeUser :
                crmeUserList) {

            String phoneNumber = crmeUser.getPhoneNumber();
            Log.d(TAG, "phoneNumber: " + phoneNumber);

            if (TextUtils.isEmpty(phoneNumber)) {
                noTienenMessenger++;
                checkToFinish();
                continue;
            }


            contactsHelper.existPhoneNumber(crmeUser.getPhoneNumber(), new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "existPhoneNumber onDataChange: ");
                    String userKey = dataSnapshot.getValue(String.class);
                    if (TextUtils.isEmpty(userKey)) {
                        noTienenMessenger++;
                        return;
                    }

                    siTienenMessenger++;

                    userHelper.getUser(userKey, new FirebaseHelper.CompletionListener<Contact>() {
                        @Override
                        public void onSuccess(Contact data) {
                            Log.d(TAG, "getUser onSuccess: ");
                            data.save();
                            contactosObtenidos++;
                            checkToFinish();
                        }

                        @Override
                        public void onFailure(Exception ex) {
                            Log.d(TAG, "getUser onFailure: ");
                            contactosFallidosDeObtener++;
                            checkToFinish();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "existPhoneNumber onCancelled: ");
                    noTienenMessenger++;
                    checkToFinish();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            //e.printStackTrace();
            return Result.FAILURE;
        }

        return Result.SUCCESS;
    }

    private void checkToFinish() {
        if (totalCrmeUser == (noTienenMessenger + contactosObtenidos + contactosFallidosDeObtener)) {
            Log.d(TAG, "checkToFinish terminado!!!: ");
            countDownLatch.countDown();
        }
    }


}
