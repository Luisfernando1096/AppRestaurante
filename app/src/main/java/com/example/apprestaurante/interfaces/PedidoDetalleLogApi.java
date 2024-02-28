package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.PedidoDetalleLog;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PedidoDetalleLogApi {
    @GET("api/pedidodetallelog/obtenerpedidoeliminado/{id}")
    Call<PedidoDetalleLog> pedidoEliminado(@Path("id") String id);
    @POST("api/pedidodetallelog/insertar")
    Call<Boolean> insertarPedidoDetalleLog(@Body PedidoDetalleLog pedidoDetalle);
    @PUT("api/pedidodetallelog/actualizar")
    Call<Boolean> ActualizarPedidoDetalleLog(@Body PedidoDetalleLog pedidoDetalle);
}
