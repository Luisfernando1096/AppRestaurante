package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Cliente;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.ClienteApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteService {
    private final ClienteApi clienteApi;

    public ClienteService() {
        this.clienteApi = ApiClient.getClient().create(ClienteApi.class);;
    }

    public void InsertarCliente(Cliente cliente, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        ClienteApi clienteApi = ApiClient.getClient().create(ClienteApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = clienteApi.insertarCliente(cliente);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    callback.onResponseBool(response);
                } else {
                    callback.onFailure("Error en la respuesta al insertar pedido.");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onFailure("Error en la solicitud al insertar pedido: " + t.getMessage());
            }
        });
    }

    public void obtenerClientes(final CallBackApi<Cliente> callback) {
        Call<List<Cliente>> call = clienteApi.obtenerClientes();
        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful()) {
                    List<Cliente> clientes = response.body();
                    callback.onResponseList(clientes);
                } else {
                    // Aquí puedes manejar el caso en que la respuesta no sea exitosa
                    callback.onFailure("Error en la respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }
}
