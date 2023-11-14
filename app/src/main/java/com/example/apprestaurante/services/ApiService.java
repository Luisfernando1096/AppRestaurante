package com.example.apprestaurante.services;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("notificar") // Ruta relativa al baseUrl
    Call<Void> enviarLista(@Body RequestBody body);
}

