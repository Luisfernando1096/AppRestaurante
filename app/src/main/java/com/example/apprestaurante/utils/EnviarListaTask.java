package com.example.apprestaurante.utils;

import android.os.AsyncTask;

import com.example.apprestaurante.ComandaGestion;
import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.services.ApiService;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnviarListaTask {

    public void enviarLista(List<PedidoDetalle> lista) {
        // Crear instancia de Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.105:4000/") // URL base de tu servicio
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear la interfaz del servicio
        ApiService apiService = retrofit.create(ApiService.class);

        // Serializar la lista de objetos a JSON
        Gson gson = new Gson();
        String jsonData = gson.toJson(lista);

        // Crear el cuerpo de la solicitud
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);

        // Hacer la llamada a la API utilizando Retrofit
        Call<Void> call = apiService.enviarLista(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Manejar la respuesta del servidor según sea necesario
                    // Puedes leer el contenido de la respuesta aquí si es necesario
                } else {
                    // Manejar un código de respuesta no exitoso si es necesario
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
            }
        });
    }
}



