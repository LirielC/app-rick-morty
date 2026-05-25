package com.example.rickandmortyapp.domain.usecase;

import android.net.Uri;

import com.example.rickandmortyapp.data.model.character.CapturedPhotoPayload;
import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.data.repository.CharacterRepository;
import com.example.rickandmortyapp.util.DateUtils;
import com.example.rickandmortyapp.util.NetworkCallback;

public class SendCapturedPhotoUseCase {
    private static final String SOURCE_CAMERA = "camera";

    private final CharacterRepository repository;

    public SendCapturedPhotoUseCase(CharacterRepository repository) {
        this.repository = repository;
    }

    public void execute(Character character, Uri uri, NetworkCallback<String> callback) {
        if (character == null || uri == null) {
            callback.onError("Falha ao enviar POST simulado.");
            return;
        }

        CapturedPhotoPayload payload = new CapturedPhotoPayload(
                character.getId(),
                character.getName(),
                uri.toString(),
                DateUtils.nowIso(),
                SOURCE_CAMERA
        );
        repository.sendCapturedPhoto(payload, callback);
    }
}
