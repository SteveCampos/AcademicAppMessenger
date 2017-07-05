package com.consultoraestrategia.messengeracademico.domain;

import android.net.Uri;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.ExecutionException;

/**
 * Created by @stevecampos on 13/06/2017.
 */

public class FirebaseImageStorage {

    private static final String REF_CHAT_IMAGES = "chat_images";
    private FirebaseStorage storage;
    private static FirebaseImageStorage INSTANCE;

    public static FirebaseImageStorage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseImageStorage();
        }
        return INSTANCE;
    }

    private FirebaseImageStorage() {
        FirebaseApp app = FirebaseApp.getInstance();
        if (app != null) {
            this.storage = FirebaseStorage.getInstance(app);
        }
    }

    public void uploadImage(Uri uri, OnSuccessListener<UploadTask.TaskSnapshot> onSuccessListener, OnProgressListener<UploadTask.TaskSnapshot> onProgressListener, OnFailureListener onFailureListener) {
        if (storage != null) {
            // Get a reference to store image at chat_images/<FILENAME>
            StorageReference pictureRef = storage.getReference().child(REF_CHAT_IMAGES).child(uri.getLastPathSegment());
            // Upload file to Firebase Storage
            pictureRef.putFile(uri)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnProgressListener(onProgressListener)
                    .addOnFailureListener(onFailureListener);
        } else {
            onFailureListener.onFailure(new Exception("storage reference is null!!!"));
        }
    }

}
