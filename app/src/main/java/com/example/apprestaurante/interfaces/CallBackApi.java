package com.example.apprestaurante.interfaces;

import java.util.List;

public interface CallBackApi<T> {
    void onResponse(T response);
    void onResponseList(List<T> response);
    void onFailure(String errorMessage);
}
