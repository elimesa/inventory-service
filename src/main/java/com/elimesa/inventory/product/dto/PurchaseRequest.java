package com.elimesa.inventory.product.dto;

import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @Positive(message = "Quantity must be greater than zero")
        int quantity
) {
}
