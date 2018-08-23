package com.consultoraestrategia.messengeracademico.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.Grupo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseGroup extends FirebaseHelper {
    private static final String TAG = FirebaseGroup.class.getSimpleName();

    public static final String PATH_GROUPS_FROM_USER = "user-group";
    public static final String PATH_GROUPS_FROM_PHONENUMBER = "phonenumber-groups";
    public static final String PATH_GROUPS = "groups";
    public static final String PATH_GROUP = "group";
    public static final String PATH_GROUP_CRME = "group-crme";
    public static final String PATH_CRME_MEBER = "crme-member";

    public static FirebaseGroup mInstance = null;

    public static FirebaseGroup getInstance() {
        if (mInstance == null) {
            mInstance = new FirebaseGroup();
        }
        return mInstance;
    }

    private FirebaseGroup() {
        super();
    }


    public void deleteGroups(String userId, final CompletionListener<Boolean> listener) {
        Log.d(TAG, "deleteGroups: ");
        getDatabase()
                .getReference()
                .child(PATH_GROUPS_FROM_USER)
                .child(userId)
                .child(PATH_GROUP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "deleteGroups onDataChange: ");
                        if (dataSnapshot.exists()) {
                            Map<String, Object> mapToDelete = new ArrayMap<>();
                            for (DataSnapshot childSnap :
                                    dataSnapshot.getChildren()) {
                                mapToDelete.put("delete-group/" + childSnap.getKey(), true);
                            }

                            getDatabase().getReference().updateChildren(mapToDelete, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    Log.d(TAG, "onComplete: ");
                                    if (databaseError != null) {
                                        listener.onFailure(new Exception(databaseError.getMessage()));
                                        return;
                                    }

                                    listener.onSuccess(Boolean.TRUE);
                                }
                            });
                            return;
                        }

                        listener.onSuccess(Boolean.TRUE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "deleteGroups onCancelled: ");
                        listener.onFailure(new Exception(databaseError.getMessage()));
                    }
                });
    }

    private boolean isGettingGroups = false;
    private long gruposSize = 0;
    private int countGruposObtenidos = 0;
    private List<Grupo> gruposObtenidos = new ArrayList<>();

    private void resetDefaultGruposValues() {
        isGettingGroups = true;
        gruposSize = 0;
        countGruposObtenidos = 0;
        gruposObtenidos = new ArrayList<>();
    }

    public void getGroups(String phoneNumber, final CompletionListener<List<Grupo>> listener) {
        Log.d(TAG, "getGroups: ");
        /*if (isGettingGroups) {
            listener.onFailure(new Exception("Already getting groups!!!"));
            return;
        }*/
        resetDefaultGruposValues();

        DatabaseReference groupsRef = getDatabase()
                .getReference(PATH_GROUPS_FROM_PHONENUMBER)
                .child(phoneNumber)
                .child(PATH_GROUPS);

        groupsRef.keepSynced(true);//Sincronizar el nodo grupos del usuario!!!

        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getGroups onDataChange: ");
                if (!dataSnapshot.exists()) {
                    resetDefaultGruposValues();
                    listener.onSuccess(new ArrayList<Grupo>());
                    return;
                }


                gruposSize = dataSnapshot.getChildrenCount();

                if (gruposSize <= 0) {
                    listener.onSuccess(new ArrayList<Grupo>());
                    resetDefaultGruposValues();
                    return;
                }

                for (DataSnapshot childSnap :
                        dataSnapshot.getChildren()) {
                    String groupId = childSnap.getKey();
                    Boolean isActive = childSnap.getValue(Boolean.class);

                    getGroupsOneByOne(groupId, listener);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getGroups onCancelled: ");
                listener.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }

    private void getGroupsOneByOne(String groupId, final CompletionListener<List<Grupo>> listener) {
        Log.d(TAG, "getGroupsOneByOne: ");
        getGroup(groupId, new CompletionListener<Grupo>() {
            @Override
            public void onSuccess(Grupo data) {
                Log.d(TAG, "getGroupsOneByOne onSuccess: ");
                countGruposObtenidos++;
                if (data != null) {
                    gruposObtenidos.add(data);
                }

                if (gruposSize == countGruposObtenidos) {
                    if (listener != null) {
                        listener.onSuccess(gruposObtenidos);
                        resetDefaultGruposValues();
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Exception ex) {
                countGruposObtenidos++;
                Log.d(TAG, "getGroupsOneByOne onFailure: ");
                if (listener != null) {
                    listener.onFailure(ex);
                }
            }
        });
    }

    public void getGroup(String id, final CompletionListener<Grupo> listener) {
        Log.d(TAG, "getGroup: " + id);
        DatabaseReference groupRef = getDatabase().getReference()
                .child(PATH_GROUP)
                .child(id);

        groupRef.keepSynced(true);//Mantener en caché el grupo

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getGroup onDataChange: ");

                if (!dataSnapshot.exists()) {
                    if (listener != null) listener.onSuccess(null);
                    return;
                }

                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                grupo.setUid(dataSnapshot.getKey());
                if (listener != null) listener.onSuccess(grupo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getGroup onCancelled: ");
                if (listener != null)
                    listener.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }


    private boolean isGettingIntegrantes = false;
    private long integrantesSize = 0;
    private int countIntegrantesObtenidos = 0;
    private List<CrmeUser> integrantes = new ArrayList<>();


    public void getIntegrantes(String grupoId, final CompletionListener<List<CrmeUser>> listener) {
        Log.d(TAG, "getIntegrantes: ");
        if (isGettingIntegrantes)
            listener.onFailure(new Exception("Already getting integrantes!!!"));
        resetDefaultIntegrantesValues();


        DatabaseReference membersRef = getDatabase()
                .getReference("group-crme")
                .child(grupoId)
                .child("crme-member");

        membersRef.keepSynced(true);//Mantener los miembros del grupo actualizados!!!

        membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "getIntegrantes onDataChange: ");
                if (!dataSnapshot.exists()) listener.onSuccess(new ArrayList<CrmeUser>());


                integrantesSize = dataSnapshot.getChildrenCount();

                if (integrantesSize <= 0) listener.onSuccess(new ArrayList<CrmeUser>());

                for (DataSnapshot childSnap :
                        dataSnapshot.getChildren()) {
                    String integranteId = childSnap.getKey();
                    CrmeUser crmeUser = childSnap.getValue(CrmeUser.class);
                    crmeUser.setId(integranteId);

                    Log.d(TAG, "crmeUser: " + crmeUser);

                    getUserOneByOne(crmeUser, listener);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getGroups onCancelled: ");
                listener.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }

    private void resetDefaultIntegrantesValues() {
        countIntegrantesObtenidos = 0;
        integrantesSize = 0;
        integrantes = new ArrayList<>();
        isGettingIntegrantes = false;
    }

    private void getUserOneByOne(final CrmeUser crmeUser, final CompletionListener<List<CrmeUser>> listener) {
        Log.d(TAG, "getUserOneByOne: ");
        getMessengerUserId(crmeUser.getPhoneNumber(), new CompletionListener<Contact>() {
            @Override
            public void onSuccess(Contact contact) {
                Log.d(TAG, "getUserOneByOne onSuccess:");
                countIntegrantesObtenidos++;

                if (contact != null) {
                    crmeUser.setContact(contact);
                }

                if (crmeUser.isAdmin()) {
                    integrantes.add(0, crmeUser);//Agregar al inicio de la lista
                } else {
                    integrantes.add(crmeUser);
                }

                if (integrantesSize == countIntegrantesObtenidos) {
                    if (listener != null) listener.onSuccess(integrantes);
                    resetDefaultIntegrantesValues();
                }
            }

            @Override
            public void onFailure(Exception ex) {
                countIntegrantesObtenidos++;
                Log.d(TAG, "getUserOneByOne onFailure: ");
                //if (listener != null) listener.onFailure(ex);

                integrantes.add(crmeUser);


                if (integrantesSize == countIntegrantesObtenidos) {
                    if (listener != null) listener.onSuccess(integrantes);
                    resetDefaultIntegrantesValues();
                }
            }
        });
    }

    public void getMessengerUserId(String phoneNumber, final CompletionListener<Contact> listener) {
        Log.d(TAG, "getMessengerUserId: " + phoneNumber);
        if (TextUtils.isEmpty(phoneNumber)) {
            Log.d(TAG, "phoneNumber can't be null!!!" + phoneNumber);
            if (listener != null)
                listener.onFailure(new Exception("phoneNumber can't be null!!!" + phoneNumber));
            return;
        }

        FirebaseContactsHelper contactsHelper = FirebaseContactsHelper.getInstance();
        contactsHelper.existPhoneNumber(phoneNumber, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    if (listener != null) listener.onSuccess(null);
                    return;
                }

                String userId = dataSnapshot.getValue(String.class);

                getMessengerUser(userId, listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (listener != null) listener.onFailure(new Exception(databaseError.getMessage()));
            }
        });
    }

    private void getMessengerUserOneByOne(String uid, final CompletionListener<Contact> listener) {
        getMessengerUser(uid, listener);
    }

    public void getMessengerUser(String uid, final CompletionListener<Contact> listener) {
        FirebaseContactsHelper contactsHelper = FirebaseContactsHelper.getInstance();
        contactsHelper.listenUserProfile(uid, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    listener.onFailure(new Exception("user not exist!!!"));
                    return;
                }

                Contact contact = dataSnapshot.getValue(Contact.class);
                listener.onSuccess(contact);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "getMessengerUser onCancelled: ");
                listener.onFailure(new Exception(databaseError.getMessage()));
            }
        });

    }

    public void updateGroupPersonaPhonenumber(String grupoId, String personaId, String phoneNumber, final CompletionListener<Boolean> listener) {
        Log.d(TAG, "updateGroupPersonaPhonenumber: ");
        if (TextUtils.isEmpty(personaId) || TextUtils.isEmpty(grupoId) || TextUtils.isEmpty(phoneNumber)) {
            listener.onFailure(new Exception("No se reconoce al grupo, persona, o al número de teléfono."));
            return;
        }

        Map<String, Object> mapPhoneNumber = new ArrayMap<>();
        mapPhoneNumber.put("phoneNumber", phoneNumber);

        getDatabase()
                .getReference()
                .child(PATH_GROUP_CRME)
                .child(grupoId)
                .child(PATH_CRME_MEBER)
                .child(personaId)
                .updateChildren(mapPhoneNumber, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            listener.onFailure(new Exception(databaseError.getMessage()));
                        }

                        listener.onSuccess(Boolean.TRUE);
                    }
                });

    }

    public void deleteGroupFromUser(String userUid, final String groupId, final CompletionListener<Grupo> listener) {
        getDatabase()
                .getReference()
                .child(PATH_GROUPS_FROM_USER)
                .child(userUid)
                .child(PATH_GROUPS)
                .child(groupId).setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    listener.onFailure(new Exception(databaseError.getMessage()));
                    return;
                }
                Grupo grupo = new Grupo();
                grupo.setUid(groupId);
                listener.onSuccess(grupo);
            }
        });
    }
}
