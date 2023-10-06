package com.example.apprestaurante.services;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.apprestaurante.ComandaGestion;
import com.example.apprestaurante.clases.Familia;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.FamiliaApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FamiliaService {
    private final FamiliaApi familiaApi;

    public FamiliaService() {
        familiaApi = ApiClient.getClient().create(FamiliaApi.class);
    }

    public void BuscarFamilias(final CallBackApi<Familia> callback){

        Call<List<Familia>> call = ApiClient.getClient().create(FamiliaApi.class).obtenerFamilias();
        call.enqueue(new Callback<List<Familia>>() {
            @Override
            public void onResponse(Call<List<Familia>> call, Response<List<Familia>> response) {
                // Si hay respuesta

                if (response.isSuccessful()) {
                    List<Familia> lstFamilias = response.body();
                    callback.onResponseList(lstFamilias);

                }else{
                    callback.onFailure("Fallo el isSuccessful");
                }
            }

            @Override
            public void onFailure(Call<List<Familia>> call, Throwable t) {
                // Si hay un error
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });

    }
}
