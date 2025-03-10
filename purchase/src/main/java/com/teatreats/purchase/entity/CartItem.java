package com.teatreats.purchase.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CartItem {

  @Id
  @GeneratedValue(generator = "sequence-generator")
  private int cartItemId;

  @NotNull(message = "Cart ID cannot be null")
  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false)
  @JsonBackReference
  private Cart cart;

  @NotNull(message = "Product ID cannot be null")
  private int productId;

  private float discount;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;

  @CreatedDate private Date createdAt;

  @LastModifiedDate private Date updatedAt;
}
