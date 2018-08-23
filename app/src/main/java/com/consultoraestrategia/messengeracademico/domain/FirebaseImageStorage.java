package com.consultoraestrategia.messengeracademico.domain;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    private static final String REF_CHAT_IMAGES = "chat-images";
    private static final String TAG = FirebaseImageStorage.class.getSimpleName();
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
        if (app == null) return;
        this.storage = FirebaseStorage.getInstance(app);
    }

    public interface FileListener {
        void onSuccess(String url);

        void onFailure(Exception exception);

        void onProgress(double progress);
    }

    public void uploadImage(Uri uri, final FileListener listener) {
        Log.d(TAG, "uploadImage: " + uri);
        if (storage == null || listener == null) return;
        // Get a reference to store image at chat_images/<FILENAME>
        final StorageReference pictureRef = storage.getReference().child(REF_CHAT_IMAGES).child(uri.getLastPathSegment());
        // Upload file to Firebase Storage
        pictureRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: ");
                        getDownloadUrl(pictureRef, listener);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onProgress: ");
                        double percent = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "percent: " + percent);
                        listener.onProgress(percent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                        listener.onFailure(e);
                    }
                });
    }

    private void getDownloadUrl(StorageReference pictureRef, final FileListener listener) {
        Log.d(TAG, "getDownloadUrl: ");
        if (pictureRef == null || listener == null) return;
        pictureRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onSuccess(uri.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }


}
