package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Ingrediente;
import com.example.apprestaurante.clases.Producto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IngredienteApi {
    @PUT("api/ingrediente/actualizarstock")
    Call<Boolean> actualizarStockIngrediente(@Body Ingrediente ingrediente);
    @GET("api/ingrediente/ingredientesdeproducto/{id}")
    Call<List<Ingrediente>> buscarngredientesDeProducto(@Path("id") String id);
}
