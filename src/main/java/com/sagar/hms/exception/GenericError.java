package com.sagar.hms.exception;

public record GenericError(String message) implements ApiError {
}
