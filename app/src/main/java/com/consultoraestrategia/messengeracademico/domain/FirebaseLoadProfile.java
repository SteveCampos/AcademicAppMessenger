package com.consultoraestrategia.messengeracademico.domain;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.loadProfile.listener.UploadProfileListener;
import com.consultoraestrategia.messengeracademico.profileEditName.listener.ProfileEditListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USER;

/**
 * Created by kike on 1/04/2017.
 */

public class FirebaseLoadProfile extends FirebaseHelper {

    public static final String TAG = FirebaseLoadProfile.class.getSimpleName();

    public static final String CHILD_PROFILE = "PROFILE";
    public static final String PATH_ALL = "/" + CHILD_PROFILE + "/";
    public static final String STORAGE_DATABASE = "gs://messenger-academico.appspot.com";


    private DatabaseReference reference;
    private FirebaseContactsHelper helper;

    private ValueEventListener eventListener;


    public FirebaseLoadProfile() {
        super();
        this.reference = getDatabase().getReference().child(CHILD_PROFILE);
        helper = new FirebaseContactsHelper();
    }


    public void uploadProfile(final Profile profile, final UploadProfileListener listener) {
        helper.getUserKey(profile.getmPhoneNumber(), new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot : " + dataSnapshot);
                if (dataSnapshot != null) {
                    String key = dataSnapshot.getValue(String.class);
                    if (key != null) {
                        Log.d(TAG, "key" + key);
                        profile.setUserKey(key);
                        validateUri(profile, listener);
                    } else {
                        helper.postPhoneNumber(profile.getmPhoneNumber(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    uploadProfile(profile, listener);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });

    }


    private void validateUri(final Profile profile, final UploadProfileListener listener) {
        if (profile.getPhoto().getUrl() == null) {
            // uploadImage(profile, listener);
            Photo photo = new Photo();
            profile.setPhoto(photo);
            Contact contact = new Contact();
            contact.setUserKey(profile.getUserKey());
            contact.setName(profile.getmName());
            contact.setPhoneNumber(profile.getmPhoneNumber());
            contact.setType(Contact.TYPE_MAIN_CONTACT);
            contact.save();


            postProfile(profile, listener);
            Log.d(TAG, "CUANDO EL URI ES NULL");
        } else {
            uploadImage(profile, listener);
            Log.d(TAG, "CUANDO EL URI NO ES NULL");
        }
    }


    private void saveContact(Profile profile, Uri uriPhoto, UploadProfileListener listener) {

        Contact contact = new Contact();
        contact.setUserKey(profile.getUserKey());
        contact.setName(profile.getmName());
        contact.setPhoneNumber(profile.getmPhoneNumber());
        contact.setPhotoUri(uriPhoto.toString());
        contact.setType(Contact.TYPE_MAIN_CONTACT);
        contact.save();
        postProfile(profile, listener);
    }


    private void uploadImage(final Profile profile, final UploadProfileListener listener) {

        //upload imagen to firebase
        //pot profile nodes
        final Uri uri = Uri.parse(profile.getPhoto().getUrl());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_DATABASE);
        storageRef = storageRef.child("profile").child(profile.getUserKey()).child(uri.getLastPathSegment());

        storageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri uriPhoto = taskSnapshot.getDownloadUrl();
                        Log.d(TAG, "uriPhoto : " + uriPhoto);
                        if (uriPhoto != null) {
                            Photo photo = new Photo();
                            photo.setUrl(uriPhoto.toString());
                            profile.setPhoto(photo);
                            saveContact(profile, uriPhoto, listener);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(e.getMessage());
                    }
                });

    }

    private void postProfile(final Profile profile, final UploadProfileListener listener) {
        Map<String, Object> map = new HashMap<>();
        map.put(CHILD_USER + "/" + profile.getUserKey() + "/" + CHILD_PROFILE, profile.toMap());
        getDatabase().getReference().updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.onError(databaseError.getMessage());
                } else {
                    //SUCESS!
                    listener.onSucess(profile);
                }
            }
        });
    }


    public void verificatedProfile(final String phoneNumber, ValueEventListener listener) {
        reference.child(phoneNumber).addListenerForSingleValueEvent(listener);
    }


    public void editProfileName(final Profile profile, final ProfileEditListener listener) {

        helper.getUserKey(profile.getmPhoneNumber(), new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot : " + dataSnapshot);
                if (dataSnapshot != null) {
                    final String key = dataSnapshot.getValue(String.class);
                    if (key != null) {
                        Log.d(TAG, "key" + key);
                        if (profile != null) {


                            HashMap<String, Object> result = new HashMap<>();

                                result.put("name", profile.getmName());
                                FirebaseDatabase.getInstance().getReference().child(CHILD_USER + "/" + key + "/" + CHILD_PROFILE).updateChildren(result);

                                profile.setUserKey(key);
                                Contact contact = new Contact();
                                contact.setUserKey(profile.getUserKey());
                                contact.setName(profile.getmName());
                                contact.setPhoneNumber(profile.getmPhoneNumber());
                                contact.setPhotoUri(profile.getPhoto().getUrl());
                                contact.setType(Contact.TYPE_MAIN_CONTACT);
                                contact.save();




//                            Contact contact = new Contact();
//                            contact.setUserKey(profile.getUserKey());
//                            contact.setName(profile.getmName());
//                            contact.setPhoneNumber(profile.getmPhoneNumber());
//                            contact.setPhotoUri(profile.getPhoto().getUrl());
//                            contact.setType(Contact.TYPE_MAIN_CONTACT);
//                            contact.save();
                            listener.onSucess(profile);
                            Log.d(TAG, "URI :" + profile.getPhoto().getUrl());
                        }
                    } else {
                        Log.d(TAG, "User Invalido");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }


    public void editProfileImage(final Profile profile, final ProfileEditListener listener) {

        helper.getUserKey(profile.getmPhoneNumber(), new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot : " + dataSnapshot);
                if (dataSnapshot != null) {
                    final String key = dataSnapshot.getValue(String.class);
                    if (key != null) {
                        Log.d(TAG, "key" + key);
                        if (profile != null) {
                            final HashMap<String, Object>  result = new HashMap<>();


                            profile.setUserKey(key);


                            final Uri uri = Uri.parse(profile.getPhoto().getUrl());

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            // Create a storage reference from our app
                            StorageReference storageRef = storage.getReferenceFromUrl(STORAGE_DATABASE);
                            storageRef = storageRef.child("profile").child(profile.getUserKey()).child(uri.getLastPathSegment());

                            storageRef.putFile(uri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            @SuppressWarnings("VisibleForTests") Uri uriPhoto = taskSnapshot.getDownloadUrl();
                                            Log.d(TAG, "uriPhoto : " + uriPhoto);
                                            if (uriPhoto != null) {
                                                Photo photo = new Photo();
                                                photo.setUrl(uriPhoto.toString());
                                                profile.setPhoto(photo);


                                                Contact contact = new Contact();
                                                contact.setUserKey(profile.getUserKey());
                                                contact.setName(profile.getmName());
                                                contact.setPhoneNumber(profile.getmPhoneNumber());
                                                contact.setPhotoUri(profile.getPhoto().getUrl());
                                                contact.setType(Contact.TYPE_MAIN_CONTACT);
                                                contact.save();

                                                result.put("photoUri", profile.getPhoto().getUrl());
                                                FirebaseDatabase.getInstance().getReference().child(CHILD_USER + "/" + key + "/" + CHILD_PROFILE).updateChildren(result);


                                                listener.onSucess(profile);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            listener.onError(e.getMessage());
                                        }
                                    });




                            Log.d(TAG, "URI :" + profile.getPhoto().getUrl());
                        }
                    } else {
                        Log.d(TAG, "User Invalido");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }


}