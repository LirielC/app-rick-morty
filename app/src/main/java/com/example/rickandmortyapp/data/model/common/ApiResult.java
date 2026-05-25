package com.example.rickandmortyapp.data.model.common;

public class ApiResult<T> {
    public enum Status {
        SUCCESS,
        ERROR
    }

    private final Status status;
    private final T data;
    private final String message;
    private final int code;

    private ApiResult(Status status, T data, String message, int code) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> ApiResult<T> success(T data, int code) {
        return new ApiResult<>(Status.SUCCESS, data, null, code);
    }

    public static <T> ApiResult<T> error(String message, int code) {
        return new ApiResult<>(Status.ERROR, null, message, code);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
