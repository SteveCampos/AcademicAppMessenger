package com.consultoraestrategia.messengeracademico.importData;

import android.os.AsyncTask;
import android.util.Log;

//import com.consultoraestrategia.messengeracademico.api.RestAPI;
import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.consultoraestrategia.messengeracademico.entities.Alumno;
import com.consultoraestrategia.messengeracademico.entities.AnioAcademico;
import com.consultoraestrategia.messengeracademico.entities.CargaCurso;
import com.consultoraestrategia.messengeracademico.entities.Curso;
import com.consultoraestrategia.messengeracademico.entities.Docente;
import com.consultoraestrategia.messengeracademico.entities.Entidad;
import com.consultoraestrategia.messengeracademico.entities.GeorefRolPersona;
import com.consultoraestrategia.messengeracademico.entities.Georeferencia;
import com.consultoraestrategia.messengeracademico.entities.Grado;
import com.consultoraestrategia.messengeracademico.entities.Matricula;
import com.consultoraestrategia.messengeracademico.entities.MatriculaDetalle;
import com.consultoraestrategia.messengeracademico.entities.NivelesAcademicos;
import com.consultoraestrategia.messengeracademico.entities.Organigramas;
import com.consultoraestrategia.messengeracademico.entities.Persona;
import com.consultoraestrategia.messengeracademico.entities.PersonaGeoref;
import com.consultoraestrategia.messengeracademico.entities.Relacion;
import com.consultoraestrategia.messengeracademico.entities.Rol;
import com.consultoraestrategia.messengeracademico.entities.Seccion;
import com.consultoraestrategia.messengeracademico.entities.Tipo;


import com.consultoraestrategia.messengeracademico.restApi.RestAPI;
import com.consultoraestrategia.messengeracademico.utils.StringUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONObject;

/**
 * Created by kike on 15/06/2017.
 */

public class DatosGeneralesAsyntask extends AsyncTask<String, String, String> {

    private static final String TAG = DatosGeneralesAsyntask.class.getSimpleName();
    private BEDatoGenerales beDatoGenerales;
    private JSONObject jsonObject = null;

    public DatosGeneralesAsyntask() {

    }

    @Override
    protected String doInBackground(String... params) {
        RestAPI restAPI = new RestAPI();
        Log.d(TAG, "doInBackground : " + params[0]);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            jsonObject = restAPI.f_MesengerAcademico(params[0]);
            if (StringUtils.isSuccessful(jsonObject)) {
                beDatoGenerales = mapper.readValue(StringUtils.getJsonObResult(jsonObject), BEDatoGenerales.class);
                Log.d(TAG, "jsonObjectTesting : " + beDatoGenerales);
                DatabaseDefinition database = FlowManager.getDatabase(MessengerAcademicoDatabase.class);
                Transaction transaction = database.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        if (beDatoGenerales != null) {
                            FlowManager.getModelAdapter(Alumno.class).saveAll(beDatoGenerales.getAlumno(), databaseWrapper);
                            FlowManager.getModelAdapter(AnioAcademico.class).saveAll(beDatoGenerales.getAnioAcademico(), databaseWrapper);
                            FlowManager.getModelAdapter(CargaCurso.class).saveAll(beDatoGenerales.getCargacurso(), databaseWrapper);//LLEga Nulo el Bollean
                            FlowManager.getModelAdapter(Curso.class).saveAll(beDatoGenerales.getCurso(), databaseWrapper);
                            FlowManager.getModelAdapter(Docente.class).saveAll(beDatoGenerales.getDocente(), databaseWrapper);
                            FlowManager.getModelAdapter(Entidad.class).saveAll(beDatoGenerales.getEntidad(), databaseWrapper);

                            FlowManager.getModelAdapter(Grado.class).saveAll(beDatoGenerales.getGrado(), databaseWrapper); // Tiene una lista de seccion
                            FlowManager.getModelAdapter(NivelesAcademicos.class).saveAll(beDatoGenerales.getNivelesacademicos(), databaseWrapper); // Tiene una lista de seccion
                            FlowManager.getModelAdapter(Matricula.class).saveAll(beDatoGenerales.getMatricula(), databaseWrapper);
                            FlowManager.getModelAdapter(MatriculaDetalle.class).saveAll(beDatoGenerales.getMatriculaDetalle(), databaseWrapper);
                            FlowManager.getModelAdapter(Persona.class).saveAll(beDatoGenerales.getPersona(), databaseWrapper);
                            FlowManager.getModelAdapter(Rol.class).saveAll(beDatoGenerales.getRol(), databaseWrapper);
                            FlowManager.getModelAdapter(Seccion.class).saveAll(beDatoGenerales.getSeccion(), databaseWrapper);
                            FlowManager.getModelAdapter(Tipo.class).saveAll(beDatoGenerales.getTipo(), databaseWrapper);

                            FlowManager.getModelAdapter(Georeferencia.class).saveAll(beDatoGenerales.getGeoreferencia(), databaseWrapper);
                            FlowManager.getModelAdapter(PersonaGeoref.class).saveAll(beDatoGenerales.getPersonaGeoref(), databaseWrapper);
                            FlowManager.getModelAdapter(GeorefRolPersona.class).saveAll(beDatoGenerales.getGeorefRolPersona(), databaseWrapper);
                            FlowManager.getModelAdapter(Relacion.class).saveAll(beDatoGenerales.getRelacion(), databaseWrapper);
                            FlowManager.getModelAdapter(Organigramas.class).saveAll(beDatoGenerales.getOrganigramas(), databaseWrapper);
                        }

                    }
                }).error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.d(TAG, "onError : " + transaction.name() + " Throwable :"+error);
                    }
                }).success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.d(TAG, "onSuccess : " + transaction.name() );
                    }
                })
                        .build();
                transaction.execute(); // execute
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute ");
    }

}
