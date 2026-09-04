package com.elimesa.inventory.product.controller;

import com.elimesa.inventory.product.dto.ProductPageResponse;
import com.elimesa.inventory.product.dto.ProductResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    @Test
    void shouldUseDefaultThresholdWhenNotProvided() throws Exception {
        when(productService.getLowStock(5))
                .thenReturn(List.of());

        mockMvc.perform(get("/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(productService).getLowStock(5);
    }

    @Test
    void shouldReturnBadRequestWhenThresholdIsNegative() throws Exception {
        mockMvc.perform(
                        get("/products/low-stock")
                                .param("threshold", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Threshold must be zero or greater"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldUseDefaultSearchParameters() throws Exception {
        ProductPageResponse response = new ProductPageResponse(
                List.of(),
                0,
                10,
                0L,
                0,
                true
        );

        when(productService.searchProducts(null, 0, 10))
                .thenReturn(response);

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.last").value(true));

        verify(productService).searchProducts(null, 0, 10);
    }

    @Test
    void shouldReturnBadRequestWhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("page", "-1")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Page must be zero or greater"));
        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturnBadRequestWhenSizeIsOutsideAllowedRange() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("size", "51")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Size must be between 1 and 50"));
        verifyNoInteractions(productService);
    }


}
