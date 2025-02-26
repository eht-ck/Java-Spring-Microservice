package com.example.demo.controller;

import com.example.demo.dto.ProductFilterRequest;
import com.example.demo.dto.ProductUpdateDTO;
import com.example.demo.entity.Product;
import com.example.demo.service.JWTService;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/products")
public class ProductController {

  @Autowired private ProductService productService;
  @Autowired private JWTService jwtService;

  @GetMapping
  public ResponseEntity<?> getAllProducts() {
    //    System.out.println("hello");
    return productService.getAllProducts();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable int id) {

    Product product = productService.getProductById(id);
    return ResponseEntity.ok(product);
  }

  @PostMapping()
  public ResponseEntity<?> createProduct(
      @RequestBody @Valid Product product,
      HttpServletRequest request) {
    System.out.println(product);
    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Product createdProduct = productService.createProduct(product);
    return ResponseEntity.ok(createdProduct);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteProduct(@PathVariable int id, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Unauthorized: Invalid or expired token");
    }
    productService.deleteProduct(id);
    System.out.println("HERE");
    return ResponseEntity.ok("Product Deleted Successfully!!");
  }


  @PatchMapping("/update/{id}")
  public ResponseEntity<?> updateProduct(
      @PathVariable int id,
      @Valid @RequestBody ProductUpdateDTO productUpdateDTO,
      HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token))
      return ResponseEntity.status(401).body("Unauthorized: Invalid or expired token");
    Product updatedProduct =
        productService.updateProductById(
            id,
            productUpdateDTO.getBrand(),
            productUpdateDTO.getName(),
            productUpdateDTO.getCategory(),
            productUpdateDTO.getPrice(),
            productUpdateDTO.getStockQuantity(),
            productUpdateDTO.getDescription());
    return ResponseEntity.ok(updatedProduct);
  }

  @GetMapping("/products/{id}/image")
  public ResponseEntity<Product> getProductImage(@PathVariable int id) {
    Product product = productService.getProductById(id);
    return ResponseEntity.ok(product);

  }


  @PatchMapping("/updateQuantity/{id}")
  public Product updateProductQuantity(@PathVariable int id, @RequestBody int quantityToReduce){
    return  productService.updateProductQuantity(id, quantityToReduce);
  }

//  @GetMapping("/filter-and-search")
//  public List<Product> getAllProducts(
//      @RequestParam(required = false) String brand,
//      @RequestParam(required = false) String name,
//      @RequestParam(required = false) String category,
//      @RequestParam(required = false, defaultValue = "0") String minPriceStr,
//      @RequestParam(required = false, defaultValue = "100000") String maxPriceStr,
//      @RequestParam(required = false, defaultValue = "0") String minStockStr,
//      @RequestParam(required = false, defaultValue = "100000") String maxStockStr,
//      @RequestParam(required = false, defaultValue = "name") String sortBy,
//      @RequestParam(required = false, defaultValue = "asc") String sortDirection,
//      @RequestParam(required = false) String keyword) {
//    Double minPrice = Double.parseDouble(minPriceStr);
//    Double maxPrice = Double.parseDouble(maxPriceStr);
//    Integer minStock = Integer.parseInt(minStockStr);
//    Integer maxStock = Integer.parseInt(maxStockStr);
//    return productService.getFilteredAndSearchedProducts(
//        brand,
//        name,
//        category,
//        minPrice,
//        maxPrice,
//        minStock,
//        maxStock,
//        sortBy,
//        sortDirection,
//        keyword);
//  }

  @PostMapping("/filter-and-search")
  public List<Product> getAllProducts(@RequestBody ProductFilterRequest filterRequest) {
    return productService.getFilteredAndSearchedProducts(
            filterRequest.getBrand(),
            filterRequest.getName(),
            filterRequest.getCategory(),
            filterRequest.getMinPrice(),
            filterRequest.getMaxPrice(),
            filterRequest.getMinStock(),
            filterRequest.getMaxStock(),
            filterRequest.getSortBy(),
            filterRequest.getSortDirection(),
            filterRequest.getKeyword()
    );
  }


  @PatchMapping("/increaseQuantity/{id}")
  public Product increaseProductQuantity(@PathVariable int id, @RequestBody int quantityToReduce){
    return  productService.increaseProductQuantity(id, quantityToReduce);
  }
}
