package com.elimesa.inventory.product.controller;

import com.elimesa.inventory.product.dto.PurchaseRequest;
import com.elimesa.inventory.product.entity.Product;
import com.elimesa.inventory.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnBadRequestWhenProductIsInvalid() throws Exception {
        String invalidProduct = """
                {
                "name": "",
                "price": -1,
                "stock": -5
                }
                """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProduct))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnConflictWhenProductWasModifiedConcurrently() throws Exception {
        when(productService.purchase(
                eq(1L),
                any(PurchaseRequest.class)
        )).thenThrow(
                new ObjectOptimisticLockingFailureException(Product.class, 1L)
        );

        mockMvc.perform(post("/products/1/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                         {
                        "quantity": 1
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("product was modified by another request. Please retry."
                ));
    }
}
