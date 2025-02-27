package com.teatreats.purchase.controller;

import com.teatreats.purchase.dto.PlaceOrderDTO;
import com.teatreats.purchase.entity.Status;
import com.teatreats.purchase.service.CustomerOrderService;
import com.teatreats.purchase.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/order/")
public class CustomerOrderController {

  @Autowired
  private CustomerOrderService orderService;

  @Autowired
  private JWTService jwtService;
// TODO
  @PostMapping("/placeOrder")  // REMOVE IT
  public ResponseEntity<?> placeOrder(@Valid  @RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);
    int userId = placeOrderDTO.getUserId();
    if (!jwtService.validateAllToken(token, userId)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    try {
      Optional<?> response = orderService.placeOrder(Optional.ofNullable(placeOrderDTO.getCartItemList()), Optional.ofNullable(placeOrderDTO.getCartId()), userId, placeOrderDTO.getAddress());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An error occurred while placing the order: " + e.getMessage());
    }
  }
 // API/ORDER/STATUS
  @PatchMapping("/updateOrderStatus/{orderId}")
  public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId, @RequestParam String status, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    try {
      Status statusEnum = Status.valueOf(status.toUpperCase());
      Optional<?> order = orderService.updateOrderStatus(orderId, statusEnum);
      return ResponseEntity.ok(order);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid Status Value");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An error occurred while updating the order status: " + e.getMessage());
    }
  }

  @GetMapping()
  public ResponseEntity<?> getAll(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    try {
      return ResponseEntity.ok(orderService.getAllOrder());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("An error occurred while retrieving orders: " + e.getMessage());
    }
  }
}