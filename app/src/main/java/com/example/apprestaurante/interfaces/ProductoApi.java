package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Producto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductoApi {
    @GET("api/producto/{id}")
    Call<List<Producto>> productoPorFamilia(@Path("id") String id);
    @GET("api/producto/productoporid/{id}")
    Call<Producto> productoPorId(@Path("id") String id);
    @PUT("api/producto/actualizarstock")
    Call<Boolean> actualizarStockProducto(@Body Producto producto);
    @GET("api/Producto/mostrarimagen/{id}")
    Call<ResponseBody> mostrarImagen(@Path("id") String id);
}
