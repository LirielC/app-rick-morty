package com.example.rickandmortyapp.ui.state;

import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.util.UiState;

public class CharacterProfileUiState {
    private final Character character;
    private final String capturedImageUri;
    private final UiState<String> postState;
    private final Event<Integer> postMessageEvent;

    public CharacterProfileUiState(Character character, String capturedImageUri, UiState<String> postState,
                                   Event<Integer> postMessageEvent) {
        this.character = character;
        this.capturedImageUri = capturedImageUri;
        this.postState = postState;
        this.postMessageEvent = postMessageEvent;
    }

    public static CharacterProfileUiState idle() {
        return new CharacterProfileUiState(null, null, UiState.idle(), null);
    }

    public Character getCharacter() {
        return character;
    }

    public String getCapturedImageUri() {
        return capturedImageUri;
    }

    public UiState<String> getPostState() {
        return postState;
    }

    public Event<Integer> getPostMessageEvent() {
        return postMessageEvent;
    }
}
