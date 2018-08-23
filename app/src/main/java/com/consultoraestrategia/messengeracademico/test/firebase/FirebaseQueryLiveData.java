package com.consultoraestrategia.messengeracademico.test.firebase;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "FirebaseQueryLiveData";

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();
    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            query.removeEventListener(listener);
            listenerRemovePending = false;
        }
    };

    public FirebaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        Log.d(LOG_TAG, "onActive");
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        } else {
            query.addChildEventListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, "onInactive");
        // UpdateListener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    public void removeListenerNow() {
        handler.post(removeListener);
    }

    private class MyValueEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            setValue(dataSnapshot);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}