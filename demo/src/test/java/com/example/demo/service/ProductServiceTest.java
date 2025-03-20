package com.example.demo.service;

import com.example.demo.customexception.ResourceNotFoundException;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ProductServiceTest {


    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private  ProductService productService;

    @BeforeEach
    void setUp() {
           }

    @Test
    void getAllProducts() {
        assertNotNull(productService.getAllProducts());
      }

  @Test
  void getProductById() {
    assertNotNull(productService.getProductById(706506));
        }

    @Test
gi    void createProduct() {
        Product product = new Product();
        product.setName("TEST PROD");
        product.setPrice(100);
        product.setCategory(Category.TEA);
        product.setBrand("Tetly");
        product.setImageURL("https://scontent-fra5-2.cdninstagram.com/v/t51.75761-15/483178912_18332353741094526_2442257020225236361_n.jpg?stp=dst-jpg_e35_tt6&_nc_cat=109&ccb=1-7&_nc_sid=18de74&_nc_ohc=9ZgnH7HSzXcQ7kNvgFRyRuE&_nc_oc=AdiNhonNqxaKvLI3i8ptU2itRyf64-TRmYxOgDuWskjEdDGZIhf5EXUxtEPywx0GhIECbQReFFddPnWUSgLsf2A9&_nc_zt=23&_nc_ht=scontent-fra5-2.cdninstagram.com&edm=ANo9K5cEAAAA&_nc_gid=ARDEixYEybn68Omsrw_3x8F&oh=00_AYEll65f22q0thn1GGSZZI--qjP3de2tB_DRCbQ0fmm7Hw&oe=67D7084B");
        assertNotNull(productService.createProduct(product));
      }

    @Test
    void deleteProduct() {
        ProductService productService = Mockito.mock(ProductService.class);
        productService.deleteProduct(918073);
        verify(productService, times(1)).deleteProduct(918073);
    }


    @Test
    void updateProductById() {
        assertNotNull(productService.updateProductById(936764, "Tetly", "Updated test", Category.TEA, 200.0, 10, "new description" ));
      }

    @Test
    void getFilteredAndSearchedProducts() {
        assertNotNull(productService.getFilteredAndSearchedProducts("Teagritty " , "" , "TEA",10.0,(double)1000, 0, 1000, "name", "DESC", "Black" ) );
      }

    @Test
    void updateProductQuantity() {
        assertNotNull(productService.updateProductQuantity(742605, 10));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductQuantity(101, 10);
        });
        String expectedMessage = "Product ID 101 not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));


    }

    @Test
    void increaseProductQuantity() {
        assertNotNull(productService.increaseProductQuantity(742605,10 ));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productService.increaseProductQuantity(101, 10);
        });
            String expectedMessage = "Product ID 101 not found";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));

  }
}