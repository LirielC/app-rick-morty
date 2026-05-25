package com.example.rickandmortyapp.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.rickandmortyapp.R;

import java.util.Locale;

public final class CharacterValueMapper {
    private static final String TYPE_NOT_INFORMED = "Não informado";

    private CharacterValueMapper() {
    }

    public static String toDisplayStatus(@NonNull Context context, String apiValue) {
        String normalized = normalize(apiValue);
        if ("alive".equals(normalized)) {
            return context.getString(R.string.status_alive_pt);
        }
        if ("dead".equals(normalized)) {
            return context.getString(R.string.status_dead_pt);
        }
        return context.getString(R.string.status_unknown_pt);
    }

    public static String toDisplayGender(@NonNull Context context, String apiValue) {
        String normalized = normalize(apiValue);
        if ("male".equals(normalized)) {
            return context.getString(R.string.gender_male_pt);
        }
        if ("female".equals(normalized)) {
            return context.getString(R.string.gender_female_pt);
        }
        if ("genderless".equals(normalized)) {
            return context.getString(R.string.gender_genderless_pt);
        }
        return context.getString(R.string.gender_unknown_pt);
    }

    public static String toApiStatus(String uiValue) {
        String normalized = normalize(uiValue);
        if (normalized.isEmpty() || "todos".equals(normalized)) {
            return "";
        }
        if ("vivo".equals(normalized)) {
            return "alive";
        }
        if ("morto".equals(normalized)) {
            return "dead";
        }
        if ("desconhecido".equals(normalized)) {
            return "unknown";
        }
        return normalized;
    }

    public static String toApiGender(String uiValue) {
        String normalized = normalize(uiValue);
        if (normalized.isEmpty() || "todos".equals(normalized)) {
            return "";
        }
        if ("masculino".equals(normalized)) {
            return "male";
        }
        if ("feminino".equals(normalized)) {
            return "female";
        }
        if ("sem genero".equals(normalized) || "sem gênero".equals(normalized)) {
            return "genderless";
        }
        if ("desconhecido".equals(normalized)) {
            return "unknown";
        }
        return normalized;
    }

    public static String toApiSpecies(String uiValue) {
        String normalized = normalize(uiValue);
        if (normalized.isEmpty() || "todos".equals(normalized)) {
            return "";
        }
        if ("humano".equals(normalized)) {
            return "Human";
        }
        if ("alienigena".equals(normalized) || "alienígena".equals(normalized)) {
            return "Alien";
        }
        if ("humanoide".equals(normalized)) {
            return "Humanoid";
        }
        if ("robo".equals(normalized) || "robô".equals(normalized)) {
            return "Robot";
        }
        if ("animal".equals(normalized)) {
            return "Animal";
        }
        if ("criatura mitologica".equals(normalized) || "criatura mitológica".equals(normalized)) {
            return "Mythological Creature";
        }
        if ("cronenberg".equals(normalized)) {
            return "Cronenberg";
        }
        if ("doenca".equals(normalized) || "doença".equals(normalized)) {
            return "Disease";
        }
        if ("poopybutthole".equals(normalized)) {
            return "Poopybutthole";
        }
        if ("desconhecido".equals(normalized)) {
            return "unknown";
        }
        return uiValue == null ? "" : uiValue.trim();
    }

    public static String toDisplaySpecies(@NonNull Context context, String apiValue) {
        String normalized = normalize(apiValue);
        if ("human".equals(normalized)) {
            return context.getString(R.string.species_human_pt);
        }
        if ("alien".equals(normalized)) {
            return context.getString(R.string.species_alien_pt);
        }
        if ("humanoid".equals(normalized)) {
            return context.getString(R.string.species_humanoid_pt);
        }
        if ("robot".equals(normalized)) {
            return context.getString(R.string.species_robot_pt);
        }
        if ("animal".equals(normalized)) {
            return context.getString(R.string.species_animal_pt);
        }
        if ("mythological creature".equals(normalized)) {
            return context.getString(R.string.species_mythological_creature_pt);
        }
        if ("disease".equals(normalized)) {
            return context.getString(R.string.species_disease_pt);
        }
        if ("cronenberg".equals(normalized)) {
            return context.getString(R.string.species_cronenberg);
        }
        if ("poopybutthole".equals(normalized)) {
            return context.getString(R.string.species_poopybutthole);
        }
        if ("unknown".equals(normalized) || normalized.isEmpty()) {
            return context.getString(R.string.species_unknown_pt);
        }
        return apiValue == null ? context.getString(R.string.species_unknown_pt) : apiValue.trim();
    }

    public static String toDisplayType(String apiValue) {
        String normalized = normalize(apiValue);
        if (normalized.isEmpty() || "unknown".equals(normalized) || "null".equals(normalized)) {
            return TYPE_NOT_INFORMED;
        }
        return apiValue == null ? TYPE_NOT_INFORMED : apiValue.trim();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
