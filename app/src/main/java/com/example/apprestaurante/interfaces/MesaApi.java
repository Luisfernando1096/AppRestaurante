package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Mesa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MesaApi {
    @GET("api/mesa/{id}")
    public Call<List<Mesa>> mesasPorSalon();
}
