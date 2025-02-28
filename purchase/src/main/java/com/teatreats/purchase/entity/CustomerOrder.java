package com.teatreats.purchase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.Date;
import java.util.List;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class CustomerOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int orderId;

  @NotNull(message = "User ID cannot be null")
  private int userId;

  private Double totalAmount;

  @CreatedDate private Date orderDate;

  @Enumerated(EnumType.STRING)
  private Status status;

  @LastModifiedDate private Date updatedAt;

  @NotNull(message = "Address Cannot be empty")
  @Size(min = 5)
  private String deliveryAddress;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<OrderItem> orderItems;
}