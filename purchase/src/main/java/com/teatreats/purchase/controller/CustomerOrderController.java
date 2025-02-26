package com.teatreats.purchase.controller;

import com.teatreats.purchase.dto.PlaceOrderDTO;
import com.teatreats.purchase.entity.Status;
import com.teatreats.purchase.service.CustomerOrderService;
import com.teatreats.purchase.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/order/")
public class CustomerOrderController {

  @Autowired private CustomerOrderService orderService;

  @Autowired private JWTService jwtService;

  @PostMapping("/placeOrder")
  public ResponseEntity<?> placeOrder(
      @RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {


    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    int userId = jwtService.getUserId(token);
    Optional<?> response =
        orderService.placeOrder(
            placeOrderDTO.getCartItemList(), userId, placeOrderDTO.getAddress());
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/updateOrderStatus/{orderId}")
  public ResponseEntity<Optional<?>> updateOrderStatus(@PathVariable int orderId, @RequestParam String status) {
    try {
      Status statusEnum = Status.valueOf(status.toUpperCase());
      Optional<?> order = orderService.updateOrderStatus(orderId, statusEnum);
      return ResponseEntity.ok(order);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Optional.of("Invalid Status Value!!!!"));
    }
  }


  @GetMapping()
  public ResponseEntity<?> getAll(HttpServletRequest request) {

    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    if (!jwtService.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Forbidden to the Endpoint");
    }

    return ResponseEntity.ok().body(orderService.getAllOrder());
  }
}
