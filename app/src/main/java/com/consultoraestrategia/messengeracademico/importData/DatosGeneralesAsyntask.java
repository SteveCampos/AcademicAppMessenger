package com.consultoraestrategia.messengeracademico.importData;

/**
 * Created by kike on 15/06/2017.
 */

public class DatosGeneralesAsyntask /*extends AsyncTask<String, String, String> */ {

    /*
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
*/
}
