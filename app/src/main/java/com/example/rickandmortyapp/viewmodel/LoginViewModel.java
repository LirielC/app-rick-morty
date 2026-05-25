package com.example.rickandmortyapp.viewmodel;

import android.app.Application;
import android.util.Patterns;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.auth.LoginRequest;
import com.example.rickandmortyapp.data.model.auth.LoginResponse;
import com.example.rickandmortyapp.data.repository.AuthRepository;
import com.example.rickandmortyapp.ui.state.LoginUiState;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.example.rickandmortyapp.util.SessionManager;
import com.example.rickandmortyapp.util.UiState;

public class LoginViewModel extends AndroidViewModel {
    private final MutableLiveData<LoginUiState> loginUiState = new MutableLiveData<>(LoginUiState.idle());
    private final AuthRepository authRepository = new AuthRepository();
    private final SessionManager sessionManager;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<LoginUiState> getLoginUiState() {
        return loginUiState;
    }

    public boolean hasActiveSession() {
        return sessionManager.isLoggedIn();
    }

    public void login(String email, String password) {
        LoginUiState currentState = currentState();
        if (currentState.getLoginState().getStatus() == UiState.Status.LOADING) {
            return;
        }

        String trimmedEmail = email != null ? email.trim() : "";
        String trimmedPassword = password != null ? password.trim() : "";

        if (TextUtils.isEmpty(trimmedEmail) || TextUtils.isEmpty(trimmedPassword)) {
            loginUiState.setValue(new LoginUiState(
                    UiState.error(getApplication().getString(R.string.login_empty_error)),
                    new Event<>(getApplication().getString(R.string.login_empty_error)),
                    null
            ));
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            loginUiState.setValue(new LoginUiState(
                    UiState.error(getApplication().getString(R.string.login_invalid_email_error)),
                    new Event<>(getApplication().getString(R.string.login_invalid_email_error)),
                    null
            ));
            return;
        }

        loginUiState.setValue(new LoginUiState(UiState.loading(), null, null));
        authRepository.login(new LoginRequest(trimmedEmail, trimmedPassword), new NetworkCallback<>() {
            @Override
            public void onSuccess(LoginResponse data) {
                sessionManager.saveSession(data.getToken(), data.getUser());
                loginUiState.postValue(new LoginUiState(UiState.success(data), null, new Event<>(Boolean.TRUE)));
            }

            @Override
            public void onEmpty(String message) {
                loginUiState.postValue(new LoginUiState(UiState.empty(message), new Event<>(message), null));
            }

            @Override
            public void onError(String message) {
                loginUiState.postValue(new LoginUiState(UiState.error(message), new Event<>(message), null));
            }
        });
    }

    private LoginUiState currentState() {
        LoginUiState state = loginUiState.getValue();
        return state != null ? state : LoginUiState.idle();
    }
}
