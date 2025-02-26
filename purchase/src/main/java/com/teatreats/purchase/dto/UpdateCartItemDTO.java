package com.teatreats.purchase.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateCartItemDTO {
  private int cartItemId;

  @Min(value = 1, message = "Quantity must be at least 1")
  private int quantity;
}
