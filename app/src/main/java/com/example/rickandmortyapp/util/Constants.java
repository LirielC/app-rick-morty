package com.example.rickandmortyapp.util;

public final class Constants {
    public static final String RICK_MORTY_BASE_URL = "https://rickandmortyapi.com/api";
    public static final String BACKEND_BASE_URL = "http://localhost:8080";
    public static final String JSON_PLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com";
    public static final String AUTH_LOGIN_PATH = "/api/auth/login";
    public static final String EMPLOYEES_PATH = "/api/funcionarios";
    public static final String CHARACTERS_PATH = "/character";
    public static final String POSTS_PATH = "/posts";
    public static final String PREF_NAME = "rick_morty_session";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final int MAX_CHARACTER_PAGES = 3;
    public static final int ERROR_CODE_NETWORK = -1;
    public static final int ERROR_CODE_PARSING = -2;
    public static final int ERROR_CODE_UNKNOWN = -3;

    private Constants() {
    }
}
