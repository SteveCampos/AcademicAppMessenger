package com.consultoraestrategia.messengeracademico.importGroups;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.CrmeApiImpl;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ListaPadre;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.Periodo;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseCrmeGroups;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.Value;
import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.domain.FirebaseGroup;
import com.consultoraestrategia.messengeracademico.domain.FirebaseHelper;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.Grupo;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.utils.PhonenumberUtils;
import com.consultoraestrategia.messengeracademico.workManager.ImportContactsFromCrmeUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ImportGroupPresenterImpl extends BasePresenterImpl<ImportGroupView> implements ImportGroupPresenter {
    public static final String TAG = "ImportGroupPresImpl";
    private TelephonyManager manager;

    public ImportGroupPresenterImpl(UseCaseHandler handler, Resources res, EventBus eventBus, TelephonyManager manager) {
        super(handler, res, eventBus);
        this.manager = manager;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected BasePresenter<ImportGroupView> getPresenterImpl() {
        return this;
    }


    private void deleteGroups() {
        Log.d(TAG, "deleteGroups: ");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        FirebaseGroup firebaseGroup = FirebaseGroup.getInstance();
        firebaseGroup.deleteGroups(userId, new FirebaseHelper.CompletionListener<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Log.d(TAG, "deleteGroups onSuccess: ");
                showMessage("Grupos Anteriores eliminados");
            }

            @Override
            public void onFailure(Exception ex) {
                Log.d(TAG, "deleteGroups onFailure: ");
                showMessage(ex.getMessage());
            }
        });
    }

    private void importGroups() {
        CrmeApiImpl api = CrmeApiImpl.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String phonenumber = user.getPhoneNumber();
            if (TextUtils.isEmpty(phonenumber)) return;
            api.getCrmeGroups(phonenumber, new CrmeApiImpl.Listener<ResponseCrmeGroups>() {
                @Override
                public void onSuccess(final ResponseCrmeGroups data) {
                    Log.d(TAG, "onSuccess: ");
                    boolean success = data.getSuccessful();

                    if (!success) {
                        Log.d(TAG, "success == false!!!");
                        showError("No se pudo obtener los grupos del crmeEducativo");
                        return;
                    }

                    if (data.getValue() == null) {
                        Log.d(TAG, "getValue == null!!!");
                        showError("Su número no figura como docente en el crme Educativo");
                        return;
                    }

                    if (data.getValue().isEmpty()) {
                        Log.d(TAG, "getValue isEmpty!!!");
                        showError("Su número no figura como docente en el crme Educativo");
                        return;
                    }

                    Value value = data.getValue().get(0);


                    final List<Grupo> grupos = new ArrayList<>();
                    List<Periodo> secciones = value.getPeriodos();
                    for (Periodo periodo :
                            secciones) {


                        CrmeUser admin = new CrmeUser();
                        admin.setId(String.valueOf(value.getPersonaId()));
                        admin.setTutor(false);
                        admin.setAdmin(true);
                        admin.setDisplayName(value.getNombres() + ", " + value.getApellidoPaterno() + " " + value.getApellidoMaterno());
                        admin.setPhoneNumber(PhonenumberUtils.formatPhonenumber(PhonenumberUtils.getCountryCode(manager), value.getCelular()));
                        admin.setName(value.getApellidoPaterno() + " " + value.getApellidoMaterno() + ", " + value.getNombres());

                        int tutorId = periodo.getTutorId();

                        boolean isTutor = false;
                        if (tutorId == 1) {
                            isTutor = true;
                        }

                        admin.setTutor(isTutor);


                        Grupo grupo = new Grupo();
                        grupo.setUid(admin.getId() + "_" + periodo.getPeriodoId() + "_" + periodo.getGrupoId());
                        grupo.setName(periodo.getGruponombre() + ", " + periodo.getNombre() + " - " + periodo.getNivelAcademico());


                        List<CrmeUser> integrantes = new ArrayList<>();
                        integrantes.add(admin);
                        for (ListaPadre padre :
                                periodo.getListaPadres()) {
                            CrmeUser crmeUser = new CrmeUser();
                            crmeUser.setId(String.valueOf(padre.getPersonaId()));
                            crmeUser.setDisplayName(padre.getValorHijo());
                            crmeUser.setName(padre.getApellidoPaterno() + " " + padre.getApellidoMaterno() + ", " + padre.getNombres());
                            crmeUser.setTutor(false);
                            crmeUser.setAdmin(false);

                            String phonenumber = PhonenumberUtils.formatPhonenumber(PhonenumberUtils.getCountryCode(manager), padre.getCelular());
                            crmeUser.setPhoneNumber(phonenumber);
                            integrantes.add(crmeUser);
                        }

                        grupo.setIntegrantes(integrantes);
                        grupos.add(grupo);
                    }

                    Map<String, Object> gruposMap = Grupo.toMap(grupos);
                    saveGruposOnDb(grupos);
                    FirebaseDatabase.getInstance().getReference().updateChildren(gruposMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Log.d(TAG, "onComplete: ");
                            hideProgress();
                            enableBttnImport(true);

                            if (databaseError != null) {
                                showMessage(databaseError.getMessage());
                                return;
                            }

                            String message = grupos.size() + " grupos actualizados correctamente";
                            showStatus(message);
                            showImportantMessage(message);
                        }
                    });

                }

                @Override
                public void onFailure(Exception ex) {
                    Log.d(TAG, "onFailure: " + ex);
                    showMessage(ex.getMessage());
                }
            });
        }
    }

    public void saveGruposOnDb(List<Grupo> grupos) {
        /*List<CrmeUser> crmeUserList = new ArrayList<>();
        for (Grupo grupo :
                grupos) {
            crmeUserList.addAll(grupo.getIntegrantes());
        }*/


        /*FastStoreModelTransaction<CrmeUser> trans = FastStoreModelTransaction
                .insertBuilder(FlowManager.getModelAdapter(CrmeUser.class))
                .addAll(grupo.getIntegrantes())
                .build();
        FlowManager.getDatabase(MessengerAcademicoDatabase.class).executeTransaction(trans);*/
        ProcessModelTransaction<Grupo> processModelTransaction =
                new ProcessModelTransaction.Builder<>(new ProcessModelTransaction.ProcessModel<Grupo>() {
                    @Override
                    public void processModel(Grupo grupo, DatabaseWrapper wrapper) {
                        List<CrmeUser> integrantes = grupo.getIntegrantes();
                        for (CrmeUser integrante :
                                integrantes) {
                            integrante.save();
                        }
                    }

                })
                        .processListener(new ProcessModelTransaction.OnModelProcessListener<Grupo>() {
                            @Override
                            public void onModelProcessed(long current, long total, Grupo modifiedModel) {
                                Log.d(TAG, "onModelProcessed:  current: " + current + ", total: " + total);
                                if (current == (total - 1)) {
                                    Log.d(TAG, "onModelProcessed finished!!!");

                                    OneTimeWorkRequest compressionWork =
                                            new OneTimeWorkRequest.Builder(ImportContactsFromCrmeUsers.class)
                                                    .build();
                                    WorkManager.getInstance().enqueue(compressionWork);
                                }
                            }
                        })
                        .addAll(grupos).build();
        DatabaseDefinition database = FlowManager.getDatabase(MessengerAcademicoDatabase.class);
        Transaction transaction = database.beginTransactionAsync(processModelTransaction).build();
        transaction.execute();


    }

    private void showError(String error) {
        enableBttnImport(true);
        hideProgress();
        showImportantMessage(error);
    }


    private void enableBttnImport(boolean enabled) {
        if (view != null) view.enableImportBttn(enabled);
    }

    @Override
    public void importBttnClicked() {
        Log.d(TAG, "importBttnClicked");
        /*showDialogToDeleteGroups(
                "¿Desea eliminar los grupos anteriormente creados?",
                "Eliga OK para eliminar los grupos anteriores, o CANCELAR para conservarlos"
        );*/
        onDialogNegativeClicked();
    }

    private void showDialogToDeleteGroups(String title, String message) {
        if (view != null) view.showDialogToDeleteGroups(title, message);
    }

    @Override
    public void onDialogNegativeClicked() {
        Log.d(TAG, "onDialogNegativeClicked: ");
        enableBttnImport(false);
        showProgress();
        importGroups();
    }

    @Override
    public void onDialogPositiveClicked() {
        Log.d(TAG, "onDialogPositiveClicked: ");
        enableBttnImport(false);
        showProgress();
        //deleteGroups();
        importGroups();
    }

    private void showStatus(String status) {
        if (view != null) view.showTxtStatus(status);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(Object event) {
        Log.d(TAG, "onEventMainThread");
    }
}
