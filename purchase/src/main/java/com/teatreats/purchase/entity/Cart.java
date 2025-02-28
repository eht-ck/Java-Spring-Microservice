package com.teatreats.purchase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Cart {
  @Id
  @GeneratedValue(generator = "sequence-generator")
  private int cartId;

  @NotNull(message = "User ID cannot be null")
  private int userId;

  @CreatedDate private Date createdAt;

  @LastModifiedDate private Date updatedAt;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<CartItem> cartItems;
}
