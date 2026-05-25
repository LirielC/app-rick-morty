package com.example.rickandmortyapp.util;

public interface NetworkCallback<T> {
    void onSuccess(T data);

    void onEmpty(String message);

    void onError(String message);
}
