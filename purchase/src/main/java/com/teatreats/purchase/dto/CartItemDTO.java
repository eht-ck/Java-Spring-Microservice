package com.teatreats.purchase.dto;

import com.teatreats.purchase.entity.Cart;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Data
public class CartItemDTO {

  @NotNull(message = "Product ID cannot be null")
  private int productId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;

}
