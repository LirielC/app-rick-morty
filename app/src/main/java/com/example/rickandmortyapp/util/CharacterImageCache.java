package com.example.rickandmortyapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CharacterImageCache {
    private static final String PREF_NAME = "character_image_cache";
    private static final String KEY_PREFIX = "character_image_";
    private static final Map<Integer, String> IMAGE_OVERRIDES = new ConcurrentHashMap<>();

    private CharacterImageCache() {
    }

    public static void put(Context context, int characterId, String imageUri) {
        if (characterId <= 0 || imageUri == null || imageUri.trim().isEmpty()) {
            return;
        }
        IMAGE_OVERRIDES.put(characterId, imageUri);
        preferences(context).edit().putString(key(characterId), imageUri).apply();
    }

    public static void put(int characterId, String imageUri) {
        if (characterId <= 0 || imageUri == null || imageUri.trim().isEmpty()) {
            return;
        }
        IMAGE_OVERRIDES.put(characterId, imageUri);
    }

    public static String get(Context context, int characterId) {
        String inMemory = IMAGE_OVERRIDES.get(characterId);
        if (inMemory != null && !inMemory.isEmpty()) {
            return inMemory;
        }
        String persisted = preferences(context).getString(key(characterId), null);
        if (persisted != null && !persisted.isEmpty()) {
            IMAGE_OVERRIDES.put(characterId, persisted);
        }
        return persisted;
    }

    public static String get(int characterId) {
        return IMAGE_OVERRIDES.get(characterId);
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String key(int characterId) {
        return KEY_PREFIX + characterId;
    }
}
