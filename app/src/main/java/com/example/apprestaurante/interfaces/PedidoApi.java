package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Pedido;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PedidoApi {
    @POST("api/pedido/insertar")
    Call<Boolean> insertarPedido(@Body Pedido pedido);
}
