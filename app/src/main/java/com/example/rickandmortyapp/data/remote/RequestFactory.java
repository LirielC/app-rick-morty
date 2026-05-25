package com.example.rickandmortyapp.data.remote;

import okhttp3.Request;
import okhttp3.RequestBody;

public final class RequestFactory {
    private RequestFactory() {
    }

    public static Request get(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static Request postJson(String url, String json) {
        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(json, ApiClient.JSON))
                .build();
    }

    public static Request putJson(String url, String json) {
        return new Request.Builder()
                .url(url)
                .put(RequestBody.create(json, ApiClient.JSON))
                .build();
    }

    public static Request delete(String url) {
        return new Request.Builder()
                .url(url)
                .delete()
                .build();
    }
}
