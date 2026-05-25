package com.example.rickandmortyapp.data.model.character;

import java.util.List;

public class CharacterResponse {
    private CharacterInfo info;
    private List<Character> results;

    public CharacterInfo getInfo() {
        return info;
    }

    public List<Character> getResults() {
        return results;
    }
}
