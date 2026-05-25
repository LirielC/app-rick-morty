package com.example.rickandmortyapp.data.remote;

import com.example.rickandmortyapp.data.model.common.ApiResult;
import com.example.rickandmortyapp.data.model.employee.Employee;
import com.example.rickandmortyapp.data.model.employee.EmployeeRequest;
import com.example.rickandmortyapp.util.Constants;
import com.example.rickandmortyapp.util.NetworkCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class EmployeeService {
    private final ApiClient apiClient;
    private final Gson gson;
    private final Type listType = new TypeToken<List<Employee>>() { }.getType();

    public EmployeeService() {
        this.apiClient = ApiClient.getInstance();
        this.gson = apiClient.getGson();
    }

    public void list(NetworkCallback<ApiResult<List<Employee>>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = RequestFactory.get(Constants.BACKEND_BASE_URL + Constants.EMPLOYEES_PATH);
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                String raw = response.body() != null ? response.body().string() : "";
                List<Employee> employees = raw.isEmpty() ? null : gson.fromJson(raw, listType);
                if (!response.isSuccessful()) {
                    callback.onSuccess(ApiResult.error(
                            extractErrorMessage(raw),
                            response.code()
                    ));
                } else {
                    callback.onSuccess(ApiResult.success(employees, response.code()));
                }
            } catch (IOException exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_NETWORK));
            }
        });
    }

    public void create(EmployeeRequest employeeRequest, NetworkCallback<ApiResult<Employee>> callback) {
        save(Constants.BACKEND_BASE_URL + Constants.EMPLOYEES_PATH, true, employeeRequest, callback);
    }

    public void update(long id, EmployeeRequest employeeRequest, NetworkCallback<ApiResult<Employee>> callback) {
        save(Constants.BACKEND_BASE_URL + Constants.EMPLOYEES_PATH + "/" + id, false, employeeRequest, callback);
    }

    public void delete(long id, NetworkCallback<ApiResult<String>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = RequestFactory.delete(Constants.BACKEND_BASE_URL + Constants.EMPLOYEES_PATH + "/" + id);
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                String raw = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    callback.onSuccess(ApiResult.success("ok", response.code()));
                } else {
                    callback.onSuccess(ApiResult.error(
                            extractErrorMessage(raw),
                            response.code()
                    ));
                }
            } catch (IOException exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_NETWORK));
            }
        });
    }

    private void save(String url, boolean isCreate, EmployeeRequest employeeRequest, NetworkCallback<ApiResult<Employee>> callback) {
        apiClient.getExecutorService().execute(() -> {
            Request request = isCreate
                    ? RequestFactory.postJson(url, gson.toJson(employeeRequest))
                    : RequestFactory.putJson(url, gson.toJson(employeeRequest));
            try (Response response = apiClient.getClient().newCall(request).execute()) {
                String raw = response.body() != null ? response.body().string() : "";
                Employee employee = raw.isEmpty() ? null : gson.fromJson(raw, Employee.class);
                if (response.isSuccessful() && employee != null) {
                    callback.onSuccess(ApiResult.success(employee, response.code()));
                } else {
                    callback.onSuccess(ApiResult.error(
                            extractErrorMessage(raw),
                            response.code()
                    ));
                }
            } catch (IOException exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_NETWORK));
            } catch (Exception exception) {
                callback.onSuccess(ApiResult.error(null, Constants.ERROR_CODE_PARSING));
            }
        });
    }

    private String extractErrorMessage(String raw) {
        try {
            Map<?, ?> map = gson.fromJson(raw, Map.class);
            Object message = map != null ? map.get("message") : null;
            return message != null ? String.valueOf(message) : null;
        } catch (Exception exception) {
            return null;
        }
    }
}
