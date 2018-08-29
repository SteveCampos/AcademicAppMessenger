package com.consultoraestrategia.messengeracademico.crme_educativo.api;

import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseCrmeGroups;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseGetInfo;
import com.consultoraestrategia.messengeracademico.crme_educativo.api.entities.ResponseUpdatePersonaPhoneNumber;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CrmeApi {
    @POST(".")
    Call<ResponseCrmeGroups> getCrmeGroups(@Body CrmeRequestBody body);
    @POST(".")
    Call<ResponseUpdatePersonaPhoneNumber> updatePersonaPhoneNumber(@Body CrmeRequestBody body);
    @POST(".")
    Call<ResponseGetInfo> getInfo(@Body CrmeRequestBody body);



}
