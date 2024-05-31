package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.MesaApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesaService {
    private final MesaApi mesaApi;

    public MesaService() {
        mesaApi = ApiClient.getClient().create(MesaApi.class);
    }

    public void ActualizarEstadoMesa(Mesa mesa, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = mesaApi.actualizarEstadoMesa(mesa);

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
