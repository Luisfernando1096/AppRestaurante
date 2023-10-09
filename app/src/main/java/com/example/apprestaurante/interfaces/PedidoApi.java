package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Pedido;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface PedidoApi {
    @POST("api/pedido/insertar")
    Call<Boolean> insertarPedido(@Body Pedido pedido);
    @GET("api/pedido/obtenerultimopedido")
    Call<Pedido> obtenerUltimoPedido();
    @PUT("api/pedido/actualizartotal")
    Call<Boolean> actualizarTotal(@Body Pedido pedido);
}
