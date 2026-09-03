package com.elimesa.inventory.common.exception;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        int status,
        String error,
        Map<String, String> fieldErrors,
        Instant timestamp
) {
}
