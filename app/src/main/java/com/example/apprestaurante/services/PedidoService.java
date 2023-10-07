package com.example.apprestaurante.services;

import android.widget.Toast;

import com.example.apprestaurante.ComandaGestion;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.FamiliaApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoService {

    private final PedidoApi pedidoApi;

    public PedidoService() {
        pedidoApi = ApiClient.getClient().create(PedidoApi.class);

    }
    public void InsertarPedido(Pedido pedido, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoApi.insertarPedido(pedido);

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

    public void obtenerUltimoPedido(final CallBackApi<Pedido> callback) {
        Call<Pedido> call = pedidoApi.obtenerUltimoPedido();
        call.enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful()) {
                    Pedido pedido = response.body();
                    callback.onResponse(pedido);
                } else {
                    callback.onFailure("Fallo el isSuccessful");
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }

}
