package com.example.rickandmortyapp.data.repository;

import com.example.rickandmortyapp.data.model.auth.LoginRequest;
import com.example.rickandmortyapp.data.model.auth.LoginResponse;
import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.remote.AuthService;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;

public class AuthRepository {
    private final AuthService authService;

    public AuthRepository() {
        this(new AuthService());
    }

    public AuthRepository(AuthService authService) {
        this.authService = authService;
    }

    public void login(LoginRequest request, NetworkCallback<LoginResponse> callback) {
        authService.login(request, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<LoginResponse> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                callback.onError(mapLoginError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private String mapLoginError(ApiResult<LoginResponse> result) {
        if (result.getCode() == 401) {
            return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                    ? result.getMessage()
                    : "Credenciais invalidas.";
        }
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Backend indisponivel. Verifique se o Grails esta rodando em " + Constants.BACKEND_BASE_URL + ".";
        }
        if (result.getCode() == Constants.ERROR_CODE_PARSING) {
            return "Nao foi possivel processar a resposta do login.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Servico de autenticacao indisponivel.";
    }
}
