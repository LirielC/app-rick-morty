package com.example.rickandmortyapp.ui.state;

import com.example.rickandmortyapp.data.model.auth.LoginResponse;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.util.UiState;

public class LoginUiState {
    private final UiState<LoginResponse> loginState;
    private final Event<String> errorMessageEvent;
    private final Event<Boolean> navigationEvent;

    public LoginUiState(UiState<LoginResponse> loginState, Event<String> errorMessageEvent,
                        Event<Boolean> navigationEvent) {
        this.loginState = loginState;
        this.errorMessageEvent = errorMessageEvent;
        this.navigationEvent = navigationEvent;
    }

    public static LoginUiState idle() {
        return new LoginUiState(UiState.idle(), null, null);
    }

    public UiState<LoginResponse> getLoginState() {
        return loginState;
    }

    public Event<String> getErrorMessageEvent() {
        return errorMessageEvent;
    }

    public Event<Boolean> getNavigationEvent() {
        return navigationEvent;
    }
}
