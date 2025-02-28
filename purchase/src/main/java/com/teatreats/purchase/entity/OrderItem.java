package com.teatreats.purchase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderItemId;

    @NotNull(message = "Order ID cannot be null")
    @ManyToOne
    @JoinColumn(name="order_id", nullable = false)
    @JsonBackReference
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

    @CreatedDate private Date createdAt;

    @LastModifiedDate private Date updatedAt;
}