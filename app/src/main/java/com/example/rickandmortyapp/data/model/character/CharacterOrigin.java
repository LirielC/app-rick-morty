package com.example.rickandmortyapp.data.model.character;

import java.io.Serializable;

public class CharacterOrigin implements Serializable {
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
