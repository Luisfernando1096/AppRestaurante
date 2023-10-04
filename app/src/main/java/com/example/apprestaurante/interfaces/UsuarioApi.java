package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Usuario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UsuarioApi {
    @GET("api/usuario/{id}")
    public Call<Usuario> ObtenerDatosUsuario(@Path("id") String id);
}
