package com.example.rickandmortyapp.data.model.character;

public class CapturedPhotoPayload {
    private final int characterId;
    private final String characterName;
    private final String capturedImagePath;
    private final String capturedAt;
    private final String source;

    public CapturedPhotoPayload(int characterId, String characterName, String capturedImagePath, String capturedAt, String source) {
        this.characterId = characterId;
        this.characterName = characterName;
        this.capturedImagePath = capturedImagePath;
        this.capturedAt = capturedAt;
        this.source = source;
    }
}
