package com.teatreats.purchase.controller;

import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.service.CartService;
import com.teatreats.purchase.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart/")
public class CartController {

  @Autowired private CartService cartService;

  @Autowired private JWTService jwtService;

  @PostMapping
  public ResponseEntity<?> createCart(@Valid @RequestBody Cart cart, HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      if (!jwtService.validateAllToken(token, cart.getUserId())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Access Forbidden to the Endpoint");
      }
      Optional<Cart> createdCart = cartService.createCart(cart);
      return ResponseEntity.ok(createdCart);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while creating the cart");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getCartById(@PathVariable int id, HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      Optional<Cart> cart = cartService.getCartById(id);
      if (cart.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
      }
      int userId = jwtService.validateAndGetUserId(token);
      if (userId != 0 && cart.get().getUserId() == userId) {
        return ResponseEntity.ok(cart);
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while retrieving the cart");
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> getCartByUserId(@PathVariable int userId, HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      if (!jwtService.validateAllToken(token, userId)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Unauthorized to access the Endpoint");
      }
      Optional<Cart> cart = cartService.getCartByUserId(userId);
      if (cart.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
      }
      return ResponseEntity.ok(cart);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An error occurred while retrieving the cart");
    }
  }
}
