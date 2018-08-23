package com.consultoraestrategia.messengeracademico.domain;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.Photo;
import com.consultoraestrategia.messengeracademico.entities.Profile;
import com.consultoraestrategia.messengeracademico.entities.User;
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

import static com.consultoraestrategia.messengeracademico.domain.FirebaseUser.CHILD_USERS;

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
        helper = FirebaseContactsHelper.getInstance();
    }

    public static void uploadUserPhoto(Uri photoUri, FirebaseImageStorage.FileListener listener) {
        FirebaseImageStorage storage = FirebaseImageStorage.getInstance();
        storage.uploadImage(photoUri, listener);
    }

    public void updateUser(User user, DatabaseReference.CompletionListener listener) {
        Map<String, Object> map = new HashMap<>();
        String uid = user.getUid();
        String displayName = user.getDisplayName();
        String email = user.getEmail();
        String phoneNumber = user.getPhoneNumber();
        Uri photoUri = user.getPhotoUri();

        map.put("/users/" + uid + "/uid", uid);

        if (displayName != null && !TextUtils.isEmpty(displayName)) {
            map.put("/users/" + uid + "/displayName", displayName);
        }
        if (email != null && !TextUtils.isEmpty(email)) {
            map.put("/users/" + uid + "/email", email);
        }
        if (photoUri != null && !Uri.EMPTY.equals(photoUri)) {
            map.put("/users/" + uid + "/photoUrl", photoUri.toString());
        }

        if (phoneNumber != null && !TextUtils.isEmpty(phoneNumber)) {
            map.put("/users/" + uid + "/phoneNumber", phoneNumber);
            map.put("/phoneNumbers/" + phoneNumber, uid);
        }

        getDatabase().getReference().updateChildren(map, listener);
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
                            FirebaseDatabase.getInstance().getReference().child(CHILD_USERS + "/" + key + "/" + CHILD_PROFILE).updateChildren(result);

                            profile.setUserKey(key);
                            Contact contact = new Contact();
                            contact.setUid(profile.getUserKey());
                            contact.setName(profile.getmName());
                            contact.setPhoneNumber(profile.getmPhoneNumber());
                            contact.setPhotoUrl(profile.getPhoto().getUrl());
                            contact.setType(Contact.TYPE_MAIN_CONTACT);
                            contact.save();


//                            Contact contact = new Contact();
//                            contact.setUid(profile.getUid());
//                            contact.setName(profile.getmName());
//                            contact.setPhoneNumber(profile.getmPhoneNumber());
//                            contact.setPhotoUrl(profile.getPhoto().getUrl());
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
                            final HashMap<String, Object> result = new HashMap<>();


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
                                            @SuppressWarnings("VisibleForTests") Uri uriPhoto = Uri.EMPTY;
                                            Log.d(TAG, "uriPhoto : " + uriPhoto);
                                            if (uriPhoto != null) {
                                                Photo photo = new Photo();
                                                photo.setUrl(uriPhoto.toString());
                                                profile.setPhoto(photo);


                                                Contact contact = new Contact();
                                                contact.setUid(profile.getUserKey());
                                                contact.setName(profile.getmName());
                                                contact.setPhoneNumber(profile.getmPhoneNumber());
                                                contact.setPhotoUrl(profile.getPhoto().getUrl());
                                                contact.setType(Contact.TYPE_MAIN_CONTACT);
                                                contact.save();

                                                result.put("photoUrl", profile.getPhoto().getUrl());
                                                FirebaseDatabase.getInstance().getReference().child(CHILD_USERS + "/" + key + "/" + CHILD_PROFILE).updateChildren(result);

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