package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductoApi {
    @GET("api/producto/{id}")
    Call<List<Producto>> productoPorFamilia(@Path("id") String id);
    @GET("api/producto/productoporid/{id}")
    Call<Producto> productoPorId(@Path("id") String id);
}
