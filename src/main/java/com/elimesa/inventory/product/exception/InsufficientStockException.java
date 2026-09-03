package com.elimesa.inventory.product.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(int available, int requested) {
        super("Insufficient stock available: "
                + available +
                " requested: "
                + requested);
    }
}
