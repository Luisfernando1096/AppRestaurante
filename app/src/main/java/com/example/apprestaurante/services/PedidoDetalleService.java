package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.interfaces.PedidoDetalleApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoDetalleService {

    private final PedidoDetalleApi pedidoDetalleApi;

    public PedidoDetalleService() {
        pedidoDetalleApi = ApiClient.getClient().create(PedidoDetalleApi.class);

    }

    public void InsertarPedidoDetalle(PedidoDetalle pedidoDetalle, CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoDetalleApi pedidoDetalleApi = ApiClient.getClient().create(PedidoDetalleApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoDetalleApi.insertarPedidoDetalle(pedidoDetalle);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                callback.onResponseBool(response);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Si hay un error en la conexión de red
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });

    }

}
