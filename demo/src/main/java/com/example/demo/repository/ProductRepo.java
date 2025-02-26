package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepo extends MongoRepository<Product,Integer> ,  ProductRepoCustom {

    // DERVIED QUERIES

//    @Query
}
