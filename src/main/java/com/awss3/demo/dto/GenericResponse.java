package com.awss3.demo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record GenericResponse(boolean success, String message, Object data, int code, LocalDateTime timestamp) {
    public static GenericResponse ok(final String message, final Object data) {
        return new GenericResponse(true, message, data, HttpStatus.OK.value(), LocalDateTime.now());
    }

    public static GenericResponse ok(final String message) {
        return new GenericResponse(true, message, new EmptyJsonBody(), HttpStatus.OK.value(), LocalDateTime.now());
    }

    @JsonSerialize
    static class EmptyJsonBody {
    }
}