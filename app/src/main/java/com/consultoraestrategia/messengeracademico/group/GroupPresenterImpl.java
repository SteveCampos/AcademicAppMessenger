package com.consultoraestrategia.messengeracademico.group;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.CrmeApiImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseUpdatePersonaPhoneNumber;
import com.consultoraestrategia.messengeracademico.domain.FirebaseGroup;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.utils.PhonenumberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.consultoraestrategia.messengeracademico.group.GroupActivity.EXTRA_GROUP_ID;
import static com.consultoraestrategia.messengeracademico.group.GroupActivity.EXTRA_GROUP_NAME;

public class GroupPresenterImpl extends BasePresenterImpl<GroupView> implements GroupPresenter {
    public static final String TAG = "GroupPresenterImpl";

    public GroupPresenterImpl(UseCaseHandler handler, Resources res, EventBus eventBus) {
        super(handler, res, eventBus);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected BasePresenter<GroupView> getPresenterImpl() {
        return this;
    }


    private String groupId;
    private String groupName;

    @Override
    public void setExtras(Bundle extras) {
        super.setExtras(extras);
        groupId = extras.getString(EXTRA_GROUP_ID, null);
        groupName = extras.getString(EXTRA_GROUP_NAME, null);
        if (TextUtils.isEmpty(groupId)) {
            showFinalMessage("El grupo no se puede encontrar!!!");
            return;
        }
        showGroupName(groupName);
        getIntegrantes(groupId);
    }

    private void showGroupName(String groupName) {
        if (view != null) view.showGroupName(groupName);
    }

    private void getIntegrantes(String groupId) {
        Log.d(TAG, "getIntegrantes: " + groupId);
        showProgress();
        FirebaseGroup firebaseGroup = FirebaseGroup.getInstance();
        firebaseGroup.getIntegrantes(groupId, new FirebaseHelper.CompletionListener<List<CrmeUser>>() {
            @Override
            public void onSuccess(List<CrmeUser> data) {
                hideProgress();
                Log.d(TAG, "onSuccess: ");
                if (data.isEmpty()) {
                    showFinalMessage("El grupo no tiene integrantes");
                    return;
                }

                showIntegrantes(data);
            }

            @Override
            public void onFailure(Exception ex) {
                hideProgress();
                Log.d(TAG, "onFailure: " + ex);
                showMessage(ex.getMessage());
            }
        });
    }

    private CrmeUser admin;

    private void showIntegrantes(List<CrmeUser> crmeUsers) {
        Log.d(TAG, "showIntegrantes: ");
        if (view != null) {
            if (!crmeUsers.isEmpty()) {
                admin = crmeUsers.get(0);
            }
            sortIntegrantes(crmeUsers);

            int adminIndexOf = crmeUsers.indexOf(admin);
            if (adminIndexOf != -1) {
                crmeUsers.remove(adminIndexOf);
                crmeUsers.add(0, admin);
            }


            view.showMemberCount(crmeUsers.size());
            view.showIntegrantes(crmeUsers);
        }
    }

    private void sortIntegrantes(List<CrmeUser> list) {
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<CrmeUser>() {
                @Override
                public int compare(final CrmeUser object1, final CrmeUser object2) {
                    if (object1.getName() == null || object2.getName() == null) return -1;
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
    }

    @Subscribe
    public void onEventMainThread(Object object) {
        Log.d(TAG, "onEventMainThread: ");
    }

    @Override
    public void onPhoneNumberSubmitted(final String phoneNumber) {
        Log.d(TAG, "onPhoneNumberSubmitted: " + phoneNumber);
        if (crmeUserActual == null) {
            showMessage(R.string.global_message_error);
            return;
        }


        final String phoneNumberE164 = PhonenumberUtils.formatPhonenumber("PE", phoneNumber);
        if (TextUtils.isEmpty(phoneNumberE164)) {
            showMessage(R.string.global_message_error);
            return;
        }

        if (phoneNumberE164.equals(crmeUserActual.getPhoneNumber())) {
            showImportantMessage("El número a actualizar es el mismo");
            return;
        }


        CrmeApiImpl crmeApi = CrmeApiImpl.getInstance();
        crmeApi.updatePersonaPhoneNumber(crmeUserActual.getId(), phoneNumberE164, new CrmeApiImpl.Listener<ResponseUpdatePersonaPhoneNumber>() {
            @Override
            public void onSuccess(ResponseUpdatePersonaPhoneNumber data) {
                if (data.getSuccessful() && data.getValue()) {
                    updateGroupPersonaPhonenumber(groupId, crmeUserActual.getId(), phoneNumberE164);
                } else {
                    showImportantMessage("No se pudo actualizar el nro de: " + crmeUserActual.getDisplayName());
                }
            }

            @Override
            public void onFailure(Exception ex) {
                Log.d(TAG, "updatePersonaPhoneNumber onFailure: " + ex);
                showImportantMessage("No se pudo actualizar el nro de: " + crmeUserActual.getDisplayName());
            }
        });
    }

    private void updateGroupPersonaPhonenumber(final String groupId, String id, String phoneNumberE164) {
        Log.d(TAG, "updateGroupPersonaPhonenumber: ");
        FirebaseGroup firebaseGroup = FirebaseGroup.getInstance();
        firebaseGroup.updateGroupPersonaPhonenumber(groupId, id, phoneNumberE164, new FirebaseHelper.CompletionListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                getIntegrantes(groupId);
                showImportantMessage("Nro de: " + crmeUserActual.getDisplayName() + " actualizado.");
            }

            @Override
            public void onFailure(Exception ex) {
                Log.d(TAG, "updateGroupPersonaPhonenumber onFailure: " + ex);
                showImportantMessage("No se pudo actualizar el nro de: " + crmeUserActual.getDisplayName());
            }
        });
    }

    public static final int REQUEST_CODE_UPDATE_PHONENUMBER = 234;
    private CrmeUser crmeUserActual;

    @Override
    public void onCrmeUserLongClicked(CrmeUser item) {
        Log.d(TAG, "onCrmeUserLongClicked: " + item);
        //Return is user is not an admin!
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (admin == null
                || currentUser == null
                || currentUser.getPhoneNumber() == null
                || admin.getPhoneNumber() == null) {
            showImportantMessage("Sólo el administrador puede editar los números del CRME");
            return;
        }

        if (!currentUser.getUid().equals(admin.getContact().getUid())
                && !admin.isTutor()) {
            showImportantMessage("Sólo el administrador puede editar los números del CRME");
            return;
        }

        crmeUserActual = item;
        showEdittextDialog(
                item.getPhoneNumber(),
                InputType.TYPE_CLASS_PHONE,
                "Actuaizar Nro de: " + item.getDisplayName(),
                item.getPhoneNumber(),
                REQUEST_CODE_UPDATE_PHONENUMBER
        );
    }

    @Override
    public void onCrmeUserSelected(CrmeUser item) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showMessage("No puede iniciar un chat consigo mismo");
            return;
        }


        String currentPhoneNumber = currentUser.getPhoneNumber();
        if (currentPhoneNumber != null && currentPhoneNumber.equals(item.getPhoneNumber())) {
            showMessage("No puede iniciar un chat consigo mismo");
            return;
        }

        String phoneNumberReceptor = item.getPhoneNumber();


        if (TextUtils.isEmpty(phoneNumberReceptor)) {
            showMessage("No puedo iniciar un chat porque el receptor no tiene número.");
            return;
        }

        if (view != null) view.launchChat(item);
    }
}
