package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Salon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SalonApi {
    @GET("api/salon/{id}")
    public Call<Salon> find(@Path("id") String id);
    @GET("api/salon")
    public Call<List<Salon>> findAll();
}
