package com.example.apprestaurante.services;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.apprestaurante.clases.Producto;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.ProductoApi;
import com.example.apprestaurante.network.ApiClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductoService {
    private final ProductoApi productoApi;

    public ProductoService() {
        productoApi = ApiClient.getClient().create(ProductoApi.class);
    }

    public void buscarProductoPorId(String id, int cantidad, final CallBackApi<Producto> callback) {
        if (cantidad >= 0) {
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

    public void ActualizarStockProducto(Producto producto, final CallBackApi<Boolean> callback) {

        // Crea una instancia de la interfaz de Retrofit para realizar la inserción
        ProductoApi productoApi = ApiClient.getClient().create(ProductoApi.class);

        // Realiza la llamada para insertar el pedido
        Call<Boolean> call = productoApi.actualizarStockProducto(producto);

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
    /*public void mostrarImagen(String id, final ImageView imageView) {
        Call<ResponseBody> call = productoApi.mostrarImagen(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Convertir el InputStream a un Bitmap
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    // Mostrar la imagen en el ImageView
                    imageView.setImageBitmap(bitmap);
                } else {
                    // Manejar el error
                    Toast.makeText(imageView.getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Manejar el error de la llamada
                Toast.makeText(imageView.getContext(), "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public void recuperarImagen(String id, final OnImageLoadListener listener) {
        Call<ResponseBody> call = productoApi.mostrarImagen(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Convertir el InputStream a un Bitmap
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        // Notificar al listener que la imagen se ha cargado correctamente
                        listener.onImageLoad(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Notificar al listener si hay un error
                        listener.onImageLoadError("Error al cargar la imagen");
                    }
                } else {
                    // Notificar al listener si hay un error
                    listener.onImageLoadError("Error al cargar la imagen");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Notificar al listener si hay un error de conexión
                listener.onImageLoadError("Error en la llamada a la API");
            }
        });
    }

    // Interfaz para manejar la carga de la imagen
    public interface OnImageLoadListener {
        void onImageLoad(Bitmap bitmap);
        void onImageLoadError(String errorMessage);
    }
}