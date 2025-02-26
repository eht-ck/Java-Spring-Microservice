package com.example.demo.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Random;

@Data
@Document(collection = "Product")
public class Product {
  @Id
  @GeneratedValue(generator = "sequence-generator")
  private int id;

  @Indexed
  @NotBlank(message = "Name is mandatory")
  @Size(max = 100, message = "Name should not exceed 100 characters")
  private String name;

  @Size(max = 500, message = "Description should not exceed 500 characters")
  private String description;

  @Indexed
  @NotNull(message = "Price is mandatory")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
  private double price;

  @Min(value = 0, message = "Stock quantity must be zero or greater")
  private int stockQuantity;

  @NotBlank(message = "Brand is mandatory")
  @Size(max = 50, message = "Brand should not exceed 50 characters")
  private String brand;

  @Enumerated(EnumType.STRING)
  @NotNull(message = "Category is mandatory")
  private Category category;

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  private String imageURL;

  public int getId() {
    return id;
  }

  public void setId() {
    Random random = new Random();
    this.id = random.nextInt(1000000);

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }


}
