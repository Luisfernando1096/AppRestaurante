package com.example.apprestaurante.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.apprestaurante.clases.PedidoDetalle;
import com.example.apprestaurante.network.ApiClient;
import com.example.apprestaurante.services.ApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
        Call<ResponseBody> call = apiService.enviarLista(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        if ("true".equals(responseBody)) {
                            Toast.makeText(context, "Finalizado con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Código de respuesta no exitoso: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
                Toast.makeText(context, "Fallo en la comunicación con el servidor", Toast.LENGTH_SHORT).show();
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
        Call<ResponseBody> call = apiService.enviarListaCompleta(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        if ("true".equals(responseBody)) {
                            Toast.makeText(context, "Finalizado con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Código de respuesta no exitoso: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
                Toast.makeText(context, "Fallo en la comunicación con el servidor", Toast.LENGTH_SHORT).show();
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
        Call<ResponseBody> call = apiService.enviarInformacion(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        if ("true".equals(responseBody)) {
                            Toast.makeText(context, "Finalizado con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al leer la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Código de respuesta no exitoso: " + response.code(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                // Manejar el fallo en la comunicación con el servidor
                t.printStackTrace();
                Toast.makeText(context, "Fallo en la comunicación con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}