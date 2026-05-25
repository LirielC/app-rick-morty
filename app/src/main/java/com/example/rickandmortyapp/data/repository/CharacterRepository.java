package com.example.rickandmortyapp.data.repository;

import com.example.rickandmortyapp.data.model.character.CapturedPhotoPayload;
import com.example.rickandmortyapp.data.model.character.CharacterResponse;
import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.remote.FakePostService;
import com.example.rickandmortyapp.data.remote.RickMortyService;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;

public class CharacterRepository {
    private final RickMortyService rickMortyService;
    private final FakePostService fakePostService;

    public CharacterRepository() {
        this(new RickMortyService(), new FakePostService());
    }

    public CharacterRepository(RickMortyService rickMortyService, FakePostService fakePostService) {
        this.rickMortyService = rickMortyService;
        this.fakePostService = fakePostService;
    }

    public void fetchCharacters(int page, String status, String gender, String species, NetworkCallback<CharacterResponse> callback) {
        rickMortyService.fetchCharacters(page, status, gender, species, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<CharacterResponse> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                if (result.getCode() == 404) {
                    callback.onEmpty("Nenhum personagem encontrado.");
                    return;
                }
                callback.onError(mapCharactersError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void sendCapturedPhoto(CapturedPhotoPayload payload, NetworkCallback<String> callback) {
        fakePostService.sendCapturedPhoto(payload, new NetworkCallback<>() {
            @Override
            public void onSuccess(ApiResult<String> result) {
                if (result.isSuccess() && result.getData() != null) {
                    callback.onSuccess(result.getData());
                    return;
                }
                callback.onError(mapFakePostError(result));
            }

            @Override
            public void onEmpty(String message) {
                callback.onEmpty(message);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private String mapCharactersError(ApiResult<CharacterResponse> result) {
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Erro ao carregar personagens da API Rick and Morty.";
        }
        if (result.getCode() == Constants.ERROR_CODE_PARSING) {
            return "Nao foi possivel processar a resposta dos personagens.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Falha ao carregar personagens.";
    }

    private String mapFakePostError(ApiResult<String> result) {
        if (result.getCode() == Constants.ERROR_CODE_NETWORK) {
            return "Erro ao enviar POST simulado.";
        }
        if (result.getCode() == Constants.ERROR_CODE_PARSING) {
            return "Falha ao enviar POST simulado.";
        }
        return result.getMessage() != null && !result.getMessage().trim().isEmpty()
                ? result.getMessage()
                : "Falha ao enviar POST simulado.";
    }
}
