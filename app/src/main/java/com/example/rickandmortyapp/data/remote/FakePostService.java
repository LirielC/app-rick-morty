package com.example.rickandmortyapp.data.remote;

import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.model.character.CapturedPhotoPayload;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class FakePostService {
    private final ApiClient apiClient;
    private final Gson gson;

    public FakePostService() {
        this.apiClient = ApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public void sendCapturedPhoto(CapturedPhotoPayload payload, NetworkCallback<ApiResult<String>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = RequestFactory.postJson(
                    Constants.JSON_PLACEHOLDER_BASE_URL + Constants.POSTS_PATH,
                    gson.toJson(payload)
            );
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                if (response.isSuccessful()) {
                    callback.onSuccess(ApiResult.success("ok", response.code()));
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
