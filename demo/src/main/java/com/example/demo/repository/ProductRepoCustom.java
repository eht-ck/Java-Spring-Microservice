package com.example.demo.repository;

import com.example.demo.entity.Product;
import java.util.List;

public interface ProductRepoCustom {

  List<Product> getFilteredAndSearchedProducts(
      String brand,
      String name,
      String category,
      Double minPrice,
      Double maxPrice,
      Integer minStock,
      Integer maxStock,
      String sortBy,
      String sortDirection,
      String keyword);
}
