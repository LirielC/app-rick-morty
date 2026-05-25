package com.example.rickandmortyapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.rickandmortyapp.data.model.auth.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";

    private final SharedPreferences preferences;
    private final Gson gson;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void saveSession(String token, User user) {
        preferences.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_USER, gson.toJson(user))
                .apply();
    }

    public boolean isLoggedIn() {
        return !getToken().isEmpty() && getUser() != null;
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }

    public User getUser() {
        String rawUser = preferences.getString(KEY_USER, null);
        if (rawUser == null || rawUser.isEmpty()) {
            return null;
        }
        return gson.fromJson(rawUser, User.class);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
