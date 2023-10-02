package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Mesa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MesaApi {
    @GET("api/mesa/{id}")
    public Call<List<Mesa>> mesasPorSalon(@Path("id") String id);
}
