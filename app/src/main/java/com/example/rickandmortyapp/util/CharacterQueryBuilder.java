package com.example.rickandmortyapp.util;

import okhttp3.HttpUrl;

public final class CharacterQueryBuilder {
    private CharacterQueryBuilder() {
    }

    public static String build(int page, String status, String gender, String species) {
        HttpUrl baseUrl = HttpUrl.parse(Constants.RICK_MORTY_BASE_URL + Constants.CHARACTERS_PATH);
        if (baseUrl == null) {
            throw new IllegalStateException("Rick and Morty base URL invalida.");
        }

        HttpUrl.Builder builder = baseUrl.newBuilder()
                .addQueryParameter("page", String.valueOf(page));

        addIfPresent(builder, "status", status);
        addIfPresent(builder, "gender", gender);
        addIfPresent(builder, "species", species);
        return builder.build().toString();
    }

    private static void addIfPresent(HttpUrl.Builder builder, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            builder.addQueryParameter(key, value.trim());
        }
    }
}
