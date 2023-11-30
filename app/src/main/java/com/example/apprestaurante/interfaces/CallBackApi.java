package com.example.apprestaurante.interfaces;

import java.util.List;

import retrofit2.Response;

public interface CallBackApi<T> {
    void onResponse(T response);
    void onResponseBool(Response<Boolean> response);
    void onResponseList(List<T> response);
    void onFailure(String errorMessage);
}
