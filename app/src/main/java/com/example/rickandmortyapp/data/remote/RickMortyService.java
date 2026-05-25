package com.example.rickandmortyapp.data.remote;

import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.model.character.CharacterResponse;
import com.example.rickandmortyapp.util.CharacterQueryBuilder;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class RickMortyService {
    private final ApiClient apiClient;
    private final Gson gson;

    public RickMortyService() {
        this.apiClient = ApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public void fetchCharacters(int page, String status, String gender, String species, NetworkCallback<ApiResult<CharacterResponse>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = RequestFactory.get(CharacterQueryBuilder.build(page, status, gender, species));
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                if (response.code() == 404) {
                    callback.onSuccess(ApiResult.error(null, response.code()));
                    return;
                }

                String raw = response.body() != null ? response.body().string() : "";
                CharacterResponse characterResponse = raw.isEmpty() ? null : gson.fromJson(raw, CharacterResponse.class);
                if (response.isSuccessful() && characterResponse != null && characterResponse.getResults() != null) {
                    callback.onSuccess(ApiResult.success(characterResponse, response.code()));
                } else {
                    callback.onSuccess(ApiResult.error(null, response.code()));
                }
            } catch (IOException exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_NETWORK));
            } catch (Exception exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_PARSING));
            }
        });
    }
}
