package com.sagar.hms.exception;

public sealed interface ApiError permits ValidationError, GenericError {
    String message();
}
