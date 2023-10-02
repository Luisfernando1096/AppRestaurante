package com.example.apprestaurante.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    public static Retrofit getClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.107:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}