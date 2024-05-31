package com.example.apprestaurante.services;

import com.example.apprestaurante.clases.Empleado;
import com.example.apprestaurante.interfaces.CallBackApi;
import com.example.apprestaurante.interfaces.EmpleadoApi;
import com.example.apprestaurante.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpleadoService {
    private final EmpleadoApi empleadoApi;

    public EmpleadoService() {
        this.empleadoApi = ApiClient.getClient().create(EmpleadoApi.class);;
    }

    public void obtenerEmpleados(final CallBackApi<Empleado> callback) {
        Call<List<Empleado>> call = empleadoApi.obtenerEmpleados();
        call.enqueue(new Callback<List<Empleado>>() {
            @Override
            public void onResponse(Call<List<Empleado>> call, Response<List<Empleado>> response) {
                if (response.isSuccessful()) {
                    List<Empleado> empleados = response.body();
                    callback.onResponseList(empleados);
                } else {
                    // Aquí puedes manejar el caso en que la respuesta no sea exitosa
                    callback.onFailure("Error en la respuesta del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Empleado>> call, Throwable t) {
                callback.onFailure("Error en conexión de red: " + t.getMessage());
            }
        });
    }
}
