package com.elimesa.inventory.product.repository;

import com.elimesa.inventory.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {


}
