package com.example.apprestaurante.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.ApiService;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EnviarListaTask {
    private Load progressDialog;
    Context context;
    public EnviarListaTask(Context context) {
        this.context = context;
        progressDialog = new Load(context, "Imprimiendo ticket...");
    }

    public void enviarLista(List<PedidoDetalle> lista) {
        if(lista.size()>0){
            progressDialog.show();
        }
        // Crear instancia de Retrofit
        Retrofit retrofit = ApiClient.getEscucha();

        // Crear la interfaz del servicio
        ApiService apiService = retrofit.create(ApiService.class);

        // Serializar la lista de objetos a JSON
        Gson gson = new Gson();
        String jsonData = gson.toJson(lista);

        System.out.println("datos: " + jsonData);

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
                progressDialog.dismiss();
                Toast.makeText(context, "Finalizado con exito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
            }
        });
    }

    public void enviarListaCompleta(List<PedidoDetalle> lista) {
        progressDialog.show();
        // Crear instancia de Retrofit
        Retrofit retrofit = ApiClient.getEscucha();
        // Crear la interfaz del servicio
        ApiService apiService = retrofit.create(ApiService.class);
        // Serializar la lista de objetos a JSON
        Gson gson = new Gson();
        String jsonData = gson.toJson(lista);
        System.out.println("datos: " + jsonData);
        // Crear el cuerpo de la solicitud
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        // Hacer la llamada a la API utilizando Retrofit
        Call<Void> call = apiService.enviarListaCompleta(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Manejar la respuesta del servidor según sea necesario
                    // Puedes leer el contenido de la respuesta aquí si es necesario
                } else {
                    // Manejar un código de respuesta no exitoso si es necesario
                }
                progressDialog.dismiss();
                Toast.makeText(context, "Finalizado con exito", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
            }
        });
    }

    public void enviarTotalPedido(List<PedidoDetalle> lista) {
        progressDialog.show();
        // Crear instancia de Retrofit
        Retrofit retrofit = ApiClient.getEscucha();
        // Crear la interfaz del servicio
        ApiService apiService = retrofit.create(ApiService.class);
        // Serializar la lista de objetos a JSON
        Gson gson = new Gson();
        String jsonData = gson.toJson(lista);
        System.out.println("datos: " + jsonData);
        // Crear el cuerpo de la solicitud
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        // Hacer la llamada a la API utilizando Retrofit
        Call<Void> call = apiService.enviarInformacion(requestBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Manejar la respuesta del servidor según sea necesario
                    // Puedes leer el contenido de la respuesta aquí si es necesario
                } else {
                    // Manejar un código de respuesta no exitoso si es necesario
                }
                progressDialog.dismiss();
                Toast.makeText(context, "Finalizado con exito", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
            }
        });
    }
}