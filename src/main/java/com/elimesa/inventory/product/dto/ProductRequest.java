package com.elimesa.inventory.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Price is required")
        @DecimalMin(
                value = "0.001",
                message = "Price must be at least 0.01"
        )
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @PositiveOrZero(
                message = "Stock must be greatr than or equal to zero"
        )
        Integer stock
) {
}
