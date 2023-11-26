package com.example.apprestaurante.interfaces;

import java.util.List;

import retrofit2.Response;

public interface CallBackApi<T> {
    default void onResponse(T response) {

    }
    default void onResponseBool(Response<Boolean> response) {

    }
    default void onResponseList(List<T> response) {

    }
    default void onFailure(String errorMessage) {

    }
}
