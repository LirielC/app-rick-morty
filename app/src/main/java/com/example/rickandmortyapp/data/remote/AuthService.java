package com.example.rickandmortyapp.data.remote;

import com.example.rickandmortyapp.data.model.auth.LoginRequest;
import com.example.rickandmortyapp.data.model.auth.LoginResponse;
import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class AuthService {
    private final ApiClient apiClient;
    private final Gson gson;

    public AuthService() {
        this.apiClient = ApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public void login(LoginRequest loginRequest, NetworkCallback<ApiResult<LoginResponse>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = RequestFactory.postJson(
                    Constants.BACKEND_BASE_URL + Constants.AUTH_LOGIN_PATH,
                    gson.toJson(loginRequest)
            );
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                String raw = response.body() != null ? response.body().string() : "";
                LoginResponse loginResponse = raw.isEmpty() ? null : gson.fromJson(raw, LoginResponse.class);
                if (response.isSuccessful() && loginResponse != null && loginResponse.isSuccess()) {
                    callback.onSuccess(ApiResult.success(loginResponse, response.code()));
                    return;
                }

                if (response.code() == 401) {
                    callback.onSuccess(ApiResult.error(
                            extractErrorMessage(raw),
                            response.code()
                    ));
                    return;
                }

                callback.onSuccess(ApiResult.error(extractErrorMessage(raw), response.code()));
            } catch (IOException exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_NETWORK));
            } catch (Exception exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_PARSING));
            }
        });
    }

    private String extractErrorMessage(String raw) {
        try {
            Map<?, ?> map = gson.fromJson(raw, Map.class);
            Object message = map != null ? map.get("message") : null;
            return message != null ? String.valueOf(message) : null;
        } catch (Exception exception) {
            return null;
        }
    }
}
