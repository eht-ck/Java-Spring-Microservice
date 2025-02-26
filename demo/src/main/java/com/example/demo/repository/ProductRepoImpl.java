package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductRepoImpl implements ProductRepoCustom {
  @Autowired private MongoTemplate mongoTemplate;

  @Override
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
    Query query = new Query();

    if (brand != null) {
      query.addCriteria(Criteria.where("brand").is(brand));
    }
    if (name != null) {
      query.addCriteria(Criteria.where("name").is(name));
    }
    if (category != null) {
      query.addCriteria(Criteria.where("category").is(category));
    }
    if (minPrice != null && maxPrice != null) {
      query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
    }
    if (minStock != null && maxStock != null) {
      query.addCriteria(Criteria.where("stockQuantity").gte(minStock).lte(maxStock));
    }

    if (keyword != null && !keyword.isEmpty()) {
      Criteria searchCriteria =
          new Criteria()
              .orOperator(
                  Criteria.where("name").regex(keyword, "i"),
                  Criteria.where("brand").regex(keyword, "i"),
                  Criteria.where("description").regex(keyword, "i"),
                  Criteria.where("category").regex(keyword, "i"));
      query.addCriteria(searchCriteria);
    }

    if (sortBy != null && !sortBy.isEmpty()) {
      Sort.Direction direction =
          "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
      query.with(Sort.by(direction, sortBy));
    }

    return mongoTemplate.find(query, Product.class);
  }

}
