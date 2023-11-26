package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Pedido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PedidoApi {
    @POST("api/pedido/insertar")
    Call<Integer> insertarPedido(@Body Pedido pedido);
    @PUT("api/pedido/actualizartotal")
    Call<Boolean> actualizarTotal(@Body Pedido pedido);
    @PUT("api/pedido/actualizarmesa")
    Call<Boolean> actualizarMesa(@Body Pedido pedido);
    @GET("api/pedido/pedidosenmesa/{id}")
    Call<List<Integer>> obtenerPedidosEnMesa(@Path("id") String id);
    @GET("api/pedido/pedidoporid/{id}")
    Call<Pedido> obtenerPedidoPorId(@Path("id") String id);
    @PUT("api/pedido/actualizarcliente")
    Call<Boolean> actualizarCliente(@Body Pedido pedido);
    @PUT("api/pedido/actualizarmesero")
    Call<Boolean> actualizarmesero(@Body Pedido pedido);
}
