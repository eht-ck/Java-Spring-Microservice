package com.teatreats.purchase.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {

  @NotBlank private String name;

  @NotBlank private String description;

  @NotNull
  @Min(0)
  private double price;

  @NotNull
  @Min(0)
  private Integer stockQuantity;

  @NotBlank private String brand;

  @NotBlank private String category;
}
