package com.example.apprestaurante.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static Retrofit retrofitEscucha;
    private final static String ipAddress = "192.168.2.109";

    public static Retrofit getClient() {
        String baseUrl = "http://" + ipAddress + ":8081/";

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static Retrofit getEscucha() {
        String baseUrl = "http://" + ipAddress + ":4000/";

        retrofitEscucha = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitEscucha;
    }
}