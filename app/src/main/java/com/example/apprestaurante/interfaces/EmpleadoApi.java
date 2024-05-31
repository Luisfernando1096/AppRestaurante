package com.example.apprestaurante.interfaces;

import com.example.apprestaurante.clases.Empleado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EmpleadoApi {
    @GET("api/empleado/obtenerempleados")
    Call<List<Empleado>> obtenerEmpleados();
}
