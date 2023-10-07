package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.PedidoDetalle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PedidoDetalleApi {
    @GET("api/pedidodetalle/{id}")
    Call<List<PedidoDetalle>> productosEnMesa(@Path("id") String id);
    @POST("api/pedidodetalle/insertar")
    Call<Boolean> insertarPedidoDetalle(@Body PedidoDetalle pedidoDetalle);

}
