package com.sagar.hms.exception;

import java.util.Map;

public record ValidationError(String message, Map<String, String> fields) implements ApiError {
}
