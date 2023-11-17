package com.example.apprestaurante.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static Retrofit retrofitEscucha;
    public static Retrofit getClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.2.105:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
    public static Retrofit getEscucha(){
        retrofitEscucha = new Retrofit.Builder()
                .baseUrl("http://192.168.2.105:4000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofitEscucha;
    }
}
