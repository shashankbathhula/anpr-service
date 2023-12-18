package com.avn.anprService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private int status;
    private T data;

    public static <T> GenericResponse<T> success(String message, T data) {
        return GenericResponse.<T>builder()
                .message(message)
                .data(data)
                .success(true)
                .status(200)
                .build();
    }

    public static <T> GenericResponse<T> error(String message, int status) {
        return GenericResponse.<T>builder()
                .message(message)
                .success(false)
                .status(status)
                .build();
    }
}
