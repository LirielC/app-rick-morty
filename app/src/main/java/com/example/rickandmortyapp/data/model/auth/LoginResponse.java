package com.example.rickandmortyapp.data.model.auth;

public class LoginResponse {
    private boolean success;
    private String token;
    private String message;
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
