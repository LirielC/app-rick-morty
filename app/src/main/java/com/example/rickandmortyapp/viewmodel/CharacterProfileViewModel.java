package com.example.rickandmortyapp.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rickandmortyapp.R;
import com.example.rickandmortyapp.data.model.character.Character;
import com.example.rickandmortyapp.data.repository.CharacterRepository;
import com.example.rickandmortyapp.domain.usecase.SendCapturedPhotoUseCase;
import com.example.rickandmortyapp.ui.state.CharacterProfileUiState;
import com.example.rickandmortyapp.util.CharacterImageCache;
import com.example.rickandmortyapp.util.Event;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.example.rickandmortyapp.util.UiState;

public class CharacterProfileViewModel extends ViewModel {
    private final MutableLiveData<CharacterProfileUiState> profileState =
            new MutableLiveData<>(CharacterProfileUiState.idle());
    private final CharacterRepository repository = new CharacterRepository();
    private final SendCapturedPhotoUseCase sendCapturedPhotoUseCase = new SendCapturedPhotoUseCase(repository);

    public LiveData<CharacterProfileUiState> getProfileState() {
        return profileState;
    }

    public void setCharacter(Character character) {
        CharacterProfileUiState currentState = currentState();
        if (currentState.getCharacter() == null) {
            profileState.setValue(new CharacterProfileUiState(
                    character,
                    currentState.getCapturedImageUri(),
                    currentState.getPostState(),
                    currentState.getPostMessageEvent()
            ));
        }
        String cachedImageUri = CharacterImageCache.get(character.getId());
        if (cachedImageUri != null && !cachedImageUri.isEmpty()) {
            profileState.setValue(new CharacterProfileUiState(
                    currentState().getCharacter(),
                    cachedImageUri,
                    currentState().getPostState(),
                    currentState().getPostMessageEvent()
            ));
        }
    }

    public void restoreCapturedImage(String imageUri) {
        if (imageUri != null && !imageUri.trim().isEmpty()) {
            CharacterProfileUiState currentState = currentState();
            profileState.setValue(new CharacterProfileUiState(
                    currentState.getCharacter(),
                    imageUri,
                    currentState.getPostState(),
                    currentState.getPostMessageEvent()
            ));
        }
    }

    public void onImageCaptured(Uri uri) {
        CharacterProfileUiState currentState = currentState();
        profileState.setValue(new CharacterProfileUiState(
                currentState.getCharacter(),
                uri.toString(),
                currentState.getPostState(),
                currentState.getPostMessageEvent()
        ));
        Character character = currentState.getCharacter();
        if (character == null) {
            return;
        }
        CharacterImageCache.put(character.getId(), uri.toString());

        profileState.setValue(new CharacterProfileUiState(character, uri.toString(), UiState.loading(), null));
        sendCapturedPhotoUseCase.execute(character, uri, new NetworkCallback<>() {
            @Override
            public void onSuccess(String data) {
                profileState.postValue(new CharacterProfileUiState(
                        character,
                        uri.toString(),
                        UiState.success(data),
                        new Event<>(R.string.post_success)
                ));
            }

            @Override
            public void onEmpty(String message) {
                profileState.postValue(new CharacterProfileUiState(
                        character,
                        uri.toString(),
                        UiState.empty(message),
                        new Event<>(R.string.post_error)
                ));
            }

            @Override
            public void onError(String message) {
                profileState.postValue(new CharacterProfileUiState(
                        character,
                        uri.toString(),
                        UiState.error(message),
                        new Event<>(R.string.post_error)
                ));
            }
        });
    }

    private CharacterProfileUiState currentState() {
        CharacterProfileUiState currentState = profileState.getValue();
        return currentState != null ? currentState : CharacterProfileUiState.idle();
    }
}
