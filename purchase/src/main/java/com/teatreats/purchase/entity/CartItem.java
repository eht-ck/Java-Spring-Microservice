package com.teatreats.purchase.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CartItem {

  @Id
  @GeneratedValue(generator = "sequence-generator")
  private int cartItemId;

  @NotNull(message = "Cart ID cannot be null")
  @ManyToOne
//  @JsonIgnore
  @JoinColumn(name = "cart_id", nullable = false)
  private Cart cart;

  @NotNull(message = "Product ID cannot be null")
  private int productId;

  @Min(value = 0, message = "Discount cannot be negative")
  private float discount;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;

  @CreatedDate private Date createdAt;

  @LastModifiedDate private Date updatedAt;

  private boolean inCart;
}
