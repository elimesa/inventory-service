package com.elimesa.inventory.product.repository;

import com.elimesa.inventory.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

@SpringBootTest
public class ProductOptimisticLockingIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void shouldRejectUpdateUsingStaleProductVersion() {
        Long productId = transactionTemplate.execute(status ->
                productRepository.saveAndFlush(
                        new Product(
                                "Laptop",
                                new BigDecimal("3500.00"),
                                10
                        )
                ).getId()
        );

        Product firstCopy = transactionTemplate.execute(status ->
                    productRepository.findById(productId).orElseThrow()
                );

        Product staleCopy = transactionTemplate.execute(status ->
                    productRepository.findById(productId).orElseThrow()
                );

        transactionTemplate.executeWithoutResult(status -> {
            firstCopy.purchase(1);
            productRepository.saveAndFlush(firstCopy);
        });

        assertThrows(
                ObjectOptimisticLockingFailureException.class,
                () -> transactionTemplate.executeWithoutResult(status -> {
                    staleCopy.purchase(1);
                    productRepository.saveAndFlush(staleCopy);
                })
        );

        Product currentProduct =
                productRepository.findById(productId).orElseThrow();

        assertEquals(9, currentProduct.getStock());
    }
}
