package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoService {
    private final ProductoApi productoApi;

    public ProductoService() {
        productoApi = ApiClient.getClient().create(ProductoApi.class);
    }

    public void buscarProductoPorId(String id, int cantidad, final CallBackApi<Producto> callback) {
        if (cantidad > 0) {
            Call<Producto> call = productoApi.productoPorId(id);
            call.enqueue(new Callback<Producto>() {
                @Override
                public void onResponse(Call<Producto> call, Response<Producto> response) {
                    if (response.isSuccessful()) {
                        Producto producto = response.body();
                        callback.onResponse(producto);
                    } else {
                        callback.onFailure("Fallo el isSuccessful");
                    }
                }

                @Override
                public void onFailure(Call<Producto> call, Throwable t) {
                    callback.onFailure("Error en conexión de red: " + t.getMessage());
                }
            });
        } else {
            callback.onFailure("No ingreso una cantidad valida");
        }
    }
}

