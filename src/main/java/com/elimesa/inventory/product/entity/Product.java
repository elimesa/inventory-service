package com.elimesa.inventory.product.entity;

import com.elimesa.inventory.product.exception.InsufficientStockException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    private int stock;

    protected Product() {}

    public Product(
            String name,
            BigDecimal price,
            int stock
    ) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
    public void update(
        String name,
        BigDecimal price,
        int stock
    ) {
       this.name = name;
       this.price = price;
       this.stock = stock;
    }

    public void purchase(
        int quantity
    ) {
        if(quantity > stock) {
            throw new InsufficientStockException(stock, quantity);
        }

        this.stock -= quantity;
    }
}
