package com.example.demo.dto;

import com.example.demo.entity.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ProductUpdateDTO {

  @Size(max = 100, message = "Name should not exceed 100 characters")
  private String brand;

  @Size(max = 100, message = "Name should not exceed 100 characters")
  private String name;

  @Enumerated(EnumType.STRING)
  private Category category;

  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
  private Double price;

  @Min(value = 0, message = "Stock quantity must be zero or greater")
  private Integer stockQuantity;

  @Size(max = 500, message = "Description should not exceed 500 characters")
  private String description;

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Double getPrice() {
    return price;
  }

  public void Double(Double price) {
    this.price = price;
  }

  public Integer getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(Integer stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
