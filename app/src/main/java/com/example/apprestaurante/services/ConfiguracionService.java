package com.example.apprestaurante.services;
import com.example.apprestaurante.clases.Configuracion;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.ConfiguracionApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracionService {
    private final ConfiguracionApi configuracionApi;

    public ConfiguracionService() {
        this.configuracionApi = ApiClient.getClient().create(ConfiguracionApi.class);
    }

    public void obetnerConfiguracion(final CallBackApi<Configuracion> callback) {
        Call<List<Configuracion>> call = configuracionApi.obtenerConfiguracion();
        call.enqueue(new Callback<List<Configuracion>>() {
            @Override
            public void onResponse(Call<List<Configuracion>> call, Response<List<Configuracion>> response) {
                if (response.isSuccessful()) {
                    List<Configuracion> configuracions = response.body();
                    callback.onResponseList(configuracions);
                } else {
                    // Aquí puedes manejar el caso en que la respuesta no sea exitosa
                    callback.onFailure("Error en la respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Configuracion>> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }
}