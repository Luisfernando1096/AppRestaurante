package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.PedidoDetalle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PedidoDetalleApi {
    @GET("api/pedidodetalle/detallesdepedido/{id}/{idPedido}")
    Call<List<PedidoDetalle>> productosEnMesa(@Path("id") String id,@Path("idPedido") String idPedido);
    @POST("api/pedidodetalle/insertar")
    Call<Boolean> insertarPedidoDetalle(@Body PedidoDetalle pedidoDetalle);
    @PUT("api/pedidodetalle/actualizarcompra")
    Call<Boolean> ActualizarCompra(@Body PedidoDetalle pedidoDetalle);
}
