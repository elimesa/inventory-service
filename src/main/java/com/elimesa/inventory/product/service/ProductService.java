package com.elimesa.inventory.product.service;

import com.elimesa.inventory.product.entity.Product;
import com.elimesa.inventory.product.exception.ProductNotFoundException;
import com.elimesa.inventory.product.repository.ProductRepository;
import com.elimesa.inventory.product.dto.ProductRequest;
import com.elimesa.inventory.product.dto.ProductResponse;
import com.elimesa.inventory.product.dto.PurchaseRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> finAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(
                        () -> new ProductNotFoundException(id)
                );
    }

    public ProductResponse create(ProductRequest productRequest) {
        Product product = new Product(
                productRequest.name(),
                productRequest.price(),
                productRequest.stock()
        );

        Product savedproduct = productRepository.save(product);

        return toResponse(savedproduct);
    }

    @Transactional
    public ProductResponse update(
            Long id,
            ProductRequest productRequest
    ) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(id)
                );
        product.update(
                productRequest.name(),
                productRequest.price(),
                productRequest.stock()
        );

        return toResponse(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow( () -> new ProductNotFoundException(id) );

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse purchase(Long id, PurchaseRequest purchaseRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow( () -> new ProductNotFoundException(id) );

        product.purchase(purchaseRequest.quantity());

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}
