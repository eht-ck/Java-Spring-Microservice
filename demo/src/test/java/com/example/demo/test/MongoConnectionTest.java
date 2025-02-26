package com.example.demo.test;

import com.example.demo.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private ProductRepo repository;

    @Test
    public void testMongoConnection() {
        long count = repository.count();
        assertThat(count).isNotNull();
    }
}