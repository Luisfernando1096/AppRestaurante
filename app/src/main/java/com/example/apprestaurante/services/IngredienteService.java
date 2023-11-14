package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Ingrediente;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.IngredienteApi;
import com.example.apprestaurante.interfaces.PedidoApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredienteService {
    private final IngredienteApi ingredienteApi;

    public IngredienteService() {
        ingredienteApi = ApiClient.getClient().create(IngredienteApi.class);

    }

    public void ActualizarStockIngrediente(Ingrediente ingrediente, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        IngredienteApi ingredienteApi = ApiClient.getClient().create(IngredienteApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = ingredienteApi.actualizarStockIngrediente(ingrediente);

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

    public void buscarngredientesDeProducto(String id, final CallBackApi<Ingrediente> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        IngredienteApi ingredienteApi = ApiClient.getClient().create(IngredienteApi.class);

        // Realiza la llamada para insertar el pedido
        Call<List<Ingrediente>> call = ingredienteApi.buscarngredientesDeProducto(id);

        call.enqueue(new Callback<List<Ingrediente>>() {
            @Override
            public void onResponse(Call<List<Ingrediente>> call, Response<List<Ingrediente>> response) {
                if (response.isSuccessful()) {
                    List<Ingrediente> lstIngredientes = response.body();
                    callback.onResponseList(lstIngredientes);

                }else{
                    callback.onFailure("Fallo el isSuccessful");
                }
            }

            @Override
            public void onFailure(Call<List<Ingrediente>> call, Throwable t) {
                // Si hay un error
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }
}
