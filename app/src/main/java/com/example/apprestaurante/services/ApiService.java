package com.example.apprestaurante.services;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("notificar") // Ruta relativa al baseUrl
    Call<ResponseBody> enviarLista(@Body RequestBody body);

    @POST("comandaCompleta") // Ruta relativa al baseUrl
    Call<ResponseBody> enviarListaCompleta(@Body RequestBody body);

    @POST("datosPrecuenta") // Ruta relativa al baseUrl
    Call<ResponseBody> enviarInformacion(@Body RequestBody body);
}

