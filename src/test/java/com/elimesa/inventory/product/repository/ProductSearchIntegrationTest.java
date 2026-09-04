package com.elimesa.inventory.product.repository;

import com.elimesa.inventory.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProductSearchIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void shouldSearchIgnoringCaseAndApplyPaginationAndSorting() {
        productRepository.saveAllAndFlush(List.of(
                new Product("Phone", new BigDecimal("900.00"), 4),
                new Product("Laptop Stand", new BigDecimal("120.00"), 5),
                new Product("Laptop", new BigDecimal("3500.00"), 10)
        ));

        Sort sort = Sort.by("name")
                .ascending()
                .and(Sort.by("id").ascending());

        Page<Product> firstPage =
                productRepository.findByNameContainingIgnoreCase(
                        "LAP",
                        PageRequest.of(0, 1, sort)
                );

        Page<Product> secondPage =
                productRepository.findByNameContainingIgnoreCase(
                        "lap",
                        PageRequest.of(1, 1, sort)
                );

        assertEquals(2L, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals("Laptop", firstPage.getContent().get(0).getName());
        assertFalse(firstPage.isLast());

        assertEquals("Laptop Stand",
                secondPage.getContent().get(0).getName());
        assertTrue(secondPage.isLast());
    }

    @Test
    void shouldReturnEmptyPageWhenNoProductMatches() {
        productRepository.saveAndFlush(
                new Product("Phone", new BigDecimal("900.00"), 4)
        );

        Page<Product> result =
                productRepository.findByNameContainingIgnoreCase(
                        "tablet",
                        PageRequest.of(0, 10)
                );

        assertTrue(result.isEmpty());
        assertEquals(0L, result.getTotalElements());
    }
}