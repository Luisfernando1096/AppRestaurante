package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Familia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FamiliaApi {
    @GET("api/familia")
    Call<List<Familia>> obtenerFamilias();
}
