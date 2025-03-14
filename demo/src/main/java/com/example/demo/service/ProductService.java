package com.example.demo.service;

import com.example.demo.customexception.ResourceNotFoundException;
import com.example.demo.entity.Category;
import com.example.demo.repository.ProductRepo;
import com.example.demo.entity.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {
  @Autowired private ProductRepo productRepository;

  public ResponseEntity<List<Product>> getAllProducts() {
    System.out.println(productRepository.findAll());
    return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
  }

  public Product getProductById(int id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
  }

  public Product createProduct(Product product) {
    product.setId();
    System.out.println(product.getImageURL());
    return productRepository.save(product);
  }



  public void deleteProduct(int id) {
    Product product = getProductById(id);
    productRepository.delete(product);
  }

  public Product updateProductById(
      int id,
      String brand,
      String name,
      Category category,
      Double price,
      Integer stockQuantity,
      String description) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    System.out.println(brand);
    if (brand != null) {
      product.setBrand(brand);
    }
    if (name != null) {
      product.setName(name);
    }
    if (category != null) {
      product.setCategory(category);
    }
    if (price != null) {
      product.setPrice(price);
    }
    if (stockQuantity != null) {
      product.setStockQuantity(stockQuantity);
    }
    if (description != null) {
      product.setDescription(description);
    }
    return productRepository.save(product);
  }

  public List<Product> getFilteredAndSearchedProducts(
      String brand,
      String name,
      String category,
      Double minPrice,
      Double maxPrice,
      Integer minStock,
      Integer maxStock,
      String sortBy,
      String sortDirection,
      String keyword) {
    return productRepository.getFilteredAndSearchedProducts(
        brand,
        name,
        category,
        minPrice,
        maxPrice,
        minStock,
        maxStock,
        sortBy,
        sortDirection,
        keyword);
  }

  public Product updateProductQuantity(int id, int quantityToReduce) {
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {
      Product product = productOptional.get();
      int newQuantity = product.getStockQuantity() - quantityToReduce;
      if (newQuantity < 0) {
        throw new IllegalArgumentException("Product ID " + id + " is out of stock!");
      }
      product.setStockQuantity(newQuantity);
      return productRepository.save(product);
    } else {
      throw new ResourceNotFoundException("Product ID " + id + " not found");
    }
  }

  public Product increaseProductQuantity(int id, int quantityToIncrease) {
    Optional<Product> productOptional = productRepository.findById(id);
    if (productOptional.isPresent()) {
      Product product = productOptional.get();
      int newQuantity = product.getStockQuantity() + quantityToIncrease;
      product.setStockQuantity(newQuantity);
      return productRepository.save(product);
    } else {
      throw new ResourceNotFoundException("Product ID " + id + " not found");
    }
  }
}
