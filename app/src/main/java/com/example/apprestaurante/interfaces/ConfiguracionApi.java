package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Configuracion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConfiguracionApi {
    @GET("api/configuracion/obtenerConfiguracion")
    Call<List<Configuracion>> obtenerConfiguracion();
}
