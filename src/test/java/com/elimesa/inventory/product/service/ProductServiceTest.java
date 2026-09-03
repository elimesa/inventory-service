package com.elimesa.inventory.product.service;

import com.elimesa.inventory.product.dto.ProductResponse;
import com.elimesa.inventory.product.dto.PurchaseRequest;
import com.elimesa.inventory.product.entity.Product;
import com.elimesa.inventory.product.exception.InsufficientStockException;
import com.elimesa.inventory.product.exception.ProductNotFoundException;
import com.elimesa.inventory.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findById(99L)
        );

        assertEquals(
                "Product not found with id 99",
                exception.getMessage()
        );

        verify(productRepository).findById(99L);

    }

    @Test
    void shouldReturnProductWhenItExists() {
        Product product = new Product(
                "Laptop",
                new BigDecimal("3500.00"),
                10
        );

        when(productRepository.findById(3L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(3L);

        assertEquals("Laptop", response.name());
        assertEquals(new BigDecimal("3500.00"), response.price());
        assertEquals(10, response.stock());

        verify(productRepository).findById(3L);
    }

    @Test
    void shouldDecreaseStockWhenPurchaseSucceeds() {
        Product product = new Product(
                "laptop",
                new BigDecimal("3500.00"),
                10
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductResponse response = productService.purchase(
                1L,
                new PurchaseRequest(2)
        );

        assertEquals(8, response.stock());
        assertEquals(8, product.getStock());

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        Product product = new Product(
                "laptop",
                new BigDecimal("3500.00"),
                3
        );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productService.purchase(
                        1L,
                        new PurchaseRequest(5)
                )
        );

        assertEquals(3, product.getStock());
        assertEquals("Insufficient stock available: 3 requested: 5",
                exception.getMessage()
        );

        verify(productRepository).findById(1L);
    }
}
