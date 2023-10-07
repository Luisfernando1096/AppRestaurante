package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MesaApi {
    @GET("api/mesa/{id}")
    public Call<List<Mesa>> mesasPorSalon(@Path("id") String id);
    @PUT("api/mesa/actualizarestadomesa")
    Call<Boolean> actualizarEstadoMesa(@Body Mesa mesa);
}
