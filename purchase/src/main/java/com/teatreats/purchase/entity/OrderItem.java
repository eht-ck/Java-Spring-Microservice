package com.teatreats.purchase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;


    @NotNull(message = "Order ID cannot be null")
    @ManyToOne     // many order-item are for one order //FETCH LAZY
    @JoinColumn(name="order_id", nullable = false)
    private CustomerOrder order;


    @NotNull
    private int productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    @Min(value = 0, message = "Discount cannot be negative")
    private float discount;

    @NotNull(message = "Product name cannot be null")
    private String productName;

    private String productDescription;

    // IMAGE STORE

}