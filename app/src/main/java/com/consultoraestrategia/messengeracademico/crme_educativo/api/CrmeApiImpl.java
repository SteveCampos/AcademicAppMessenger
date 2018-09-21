package com.consultoraestrategia.messengeracademico.crme_educativo.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseCrmeGroups;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseGetInfo;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseUpdatePersonaPhoneNumber;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.params.GetInfoParams;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.params.UpdatePersonaPhoneNumberParams;
import com.consultoraestrategia.messengeracademico.entities.GlobalSettings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CrmeApiImpl {
    // Trailing slash is needed
    public static final String TAG = "CrmeApiImpl";
    private static CrmeApiImpl mInstance = null;
    private CrmeApi api;

    public static CrmeApiImpl getInstance() {
        return new CrmeApiImpl();
    }

    private CrmeApiImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GlobalSettings.getServerUrl()+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(CrmeApi.class);
    }

    public interface Listener<T> {
        void onSuccess(T data);

        void onFailure(Exception ex);
    }

    public void getCrmeGroups(String phonenumber, final Listener<ResponseCrmeGroups> listener) {
        CrmeRequestBody<PhonenumberParams> crmeRequestBody = new CrmeRequestBody<>();
        crmeRequestBody.setMethod("fins_ListarDatosDocented");
        crmeRequestBody.setParameters(new PhonenumberParams(phonenumber));


        Call<ResponseCrmeGroups> call = api.getCrmeGroups(crmeRequestBody);
        call.enqueue(new Callback<ResponseCrmeGroups>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCrmeGroups> call, @NonNull Response<ResponseCrmeGroups> response) {
                Log.d(TAG, "getCrmeGroups onResponse: " + response);
                Log.d(TAG, "onResponse body: " + response.body());
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    //ApiError error = ErrorUtils.parseError(response);
                    Log.d(TAG, "onResponse apiError: ");
                    if (listener != null)
                        listener.onFailure(new Exception("onResponse apiError!!!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCrmeGroups> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                if (listener != null) {
                    listener.onFailure(new Exception(t.getMessage()));
                }
            }
        });

    }

    public void updatePersonaPhoneNumber(String personaId, String phoneNumber, final Listener<ResponseUpdatePersonaPhoneNumber> listener) {
        CrmeRequestBody<UpdatePersonaPhoneNumberParams> crmeRequestBody = new CrmeRequestBody<>();
        crmeRequestBody.setMethod("fins_upd_ActualizarNroPhone");
        crmeRequestBody.setParameters(new UpdatePersonaPhoneNumberParams(personaId, phoneNumber));


        Call<ResponseUpdatePersonaPhoneNumber> call = api.updatePersonaPhoneNumber(crmeRequestBody);
        call.enqueue(new Callback<ResponseUpdatePersonaPhoneNumber>() {
            @Override
            public void onResponse(@NonNull Call<ResponseUpdatePersonaPhoneNumber> call, @NonNull Response<ResponseUpdatePersonaPhoneNumber> response) {
                Log.d(TAG, "updatePersonaPhoneNumber onResponse: " + response);
                Log.d(TAG, "onResponse body: " + response.body());
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    //ApiError error = ErrorUtils.parseError(response);
                    Log.d(TAG, "onResponse apiError: ");
                    if (listener != null)
                        listener.onFailure(new Exception("onResponse apiError!!!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseUpdatePersonaPhoneNumber> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                if (listener != null) {
                    listener.onFailure(new Exception(t.getMessage()));
                }
            }
        });
    }

    public void getInfo(String phoneNumberObservador, String phoneNumberObservado, final Listener<ResponseGetInfo> listener) {
        CrmeRequestBody<GetInfoParams> crmeRequestBody = new CrmeRequestBody<>();
        crmeRequestBody.setMethod("fins_Listar");
        crmeRequestBody.setParameters(new GetInfoParams(phoneNumberObservador, phoneNumberObservado));

        Call<ResponseGetInfo> call = api.getInfo(crmeRequestBody);
        call.enqueue(new Callback<ResponseGetInfo>() {
            @Override
            public void onResponse(@NonNull Call<ResponseGetInfo> call, @NonNull Response<ResponseGetInfo> response) {
                Log.d(TAG, "getInfo onResponse: " + response);
                Log.d(TAG, "getInfo onResponse body: " + response.body());
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onSuccess(response.body());
                    }
                } else {
                    //ApiError error = ErrorUtils.parseError(response);
                    Log.d(TAG, "onResponse apiError: ");
                    if (listener != null)
                        listener.onFailure(new Exception("onResponse apiError!!!"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseGetInfo> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure");
                if (listener != null) {
                    listener.onFailure(new Exception(t.getMessage()));
                }
            }
        });
    }

}
