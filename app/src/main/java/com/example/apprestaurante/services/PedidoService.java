package com.example.apprestaurante.services;

import android.widget.Toast;

import com.example.apprestaurante.ComandaGestion;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Mesa;
import com.example.apprestaurante.clases.Pedido;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.FamiliaApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoService {

    private final PedidoApi pedidoApi;

    public PedidoService() {
        pedidoApi = ApiClient.getClient().create(PedidoApi.class);
    }
    public void InsertarPedido(Pedido pedido, final CallBackApi<Integer> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Integer> call = pedidoApi.insertarPedido(pedido);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Integer idPedido = response.body();
                    if (idPedido != null && idPedido > 0) {
                        callback.onResponse(idPedido);
                    } else {
                        callback.onFailure("El ID del pedido no es válido.");
                    }
                } else {
                    callback.onFailure("Error en la respuesta al insertar pedido.");
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                callback.onFailure("Error en la solicitud al insertar pedido: " + t.getMessage());
            }
        });
    }

    public void obtenerPedidosEnMesa(String id, final CallBackApi<Integer> callback) {
        Call<List<Integer>> call = pedidoApi.obtenerPedidosEnMesa(id);
        call.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    List<Integer> pedidos = response.body();
                    callback.onResponseList(pedidos);
                } else {
                    // Aquí puedes manejar el caso en que la respuesta no sea exitosa
                    callback.onFailure("Error en la respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }

    public void ActualizarTotal(Pedido pedido, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoApi.actualizarTotal(pedido);

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

    public void ActualizarMesa(Pedido pedido, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoApi.actualizarMesa(pedido);

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

    public void obtenerPedidoPorId(String id, final CallBackApi<Pedido> callback) {
        Call<Pedido> call = pedidoApi.obtenerPedidoPorId(id);
        call.enqueue(new Callback<Pedido>() {
            @Override
            public void onResponse(Call<Pedido> call, Response<Pedido> response) {
                if (response.isSuccessful()) {
                    Pedido pedido = response.body();
                    callback.onResponse(pedido);
                } else {
                    // Aquí puedes manejar el caso en que la respuesta no sea exitosa
                    callback.onFailure("Error en la respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Pedido> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }

    public void ActualizarCliente(Pedido pedido, final CallBackApi<Boolean> callback){
        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        PedidoApi pedidoApi = ApiClient.getClient().create(PedidoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = pedidoApi.actualizarCliente(pedido);

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
