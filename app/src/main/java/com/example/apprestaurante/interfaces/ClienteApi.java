package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Cliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ClienteApi {
    @GET("api/cliente/obtenerclientes")
    Call<List<Cliente>> obtenerClientes();

    @POST("api/cliente/insertarcliente")
    Call<Boolean> insertarCliente(@Body Cliente cliente);
}
