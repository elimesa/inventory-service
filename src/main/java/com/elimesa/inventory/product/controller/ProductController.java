package com.elimesa.inventory.product.controller;

import com.elimesa.inventory.product.dto.ProductRequest;
import com.elimesa.inventory.product.dto.ProductResponse;
import com.elimesa.inventory.product.service.ProductService;
import com.elimesa.inventory.product.dto.PurchaseRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> finAll() {
        return productService.finAll();
    }

    @GetMapping("/{id}")
    public ProductResponse finById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @PostMapping("/{id}/purchase")
    public ProductResponse purchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request
    ) {
        return productService.purchase(id, request);
    }

    @GetMapping("/low-stock")
    public List<ProductResponse> lowStock(
            @RequestParam(defaultValue = "5")
            @Min(
                    value = 0,
                    message = "Threshold must be zero or greater"
            )
            int threshold
    ) {
        return productService.getLowStock(threshold);
    }
}
