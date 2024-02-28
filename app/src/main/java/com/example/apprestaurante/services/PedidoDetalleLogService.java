package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.PedidoDetalleLog;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.PedidoDetalleLogApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoDetalleLogService {

    private final PedidoDetalleLogApi pedidoDetalleLogApi;

    public PedidoDetalleLogService() {
        pedidoDetalleLogApi = ApiClient.getClient().create(PedidoDetalleLogApi.class);

    }

    public void InsertarPedidoDetalle(PedidoDetalleLog pedidoDetalle, CallBackApi<Boolean> callback) {
        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoDetalleLogApi.insertarPedidoDetalleLog(pedidoDetalle);

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

    public void ActualizarPedidoDetalleLog(PedidoDetalleLog pedidoDetalle, final CallBackApi<Boolean> callback) {
        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoDetalleLogApi.ActualizarPedidoDetalleLog(pedidoDetalle);

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

    public void pedidoEliminado(String id, final CallBackApi<PedidoDetalleLog> callback) {
        Call<PedidoDetalleLog> call = pedidoDetalleLogApi.pedidoEliminado(id);
        call.enqueue(new Callback<PedidoDetalleLog>() {
            @Override
            public void onResponse(Call<PedidoDetalleLog> call, Response<PedidoDetalleLog> response) {
                if (response.isSuccessful()) {
                    PedidoDetalleLog pedidoDetalleLog = response.body();
                    callback.onResponse(pedidoDetalleLog);
                } else {
                    callback.onFailure("Fallo el isSuccessful");
                }
            }

            @Override
            public void onFailure(Call<PedidoDetalleLog> call, Throwable t) {
                // Si hay un error en la conexión de red
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }
}
