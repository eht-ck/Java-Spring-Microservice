package com.teatreats.purchase.controller;

import com.teatreats.purchase.dto.UpdateOrderStatusDTO;
import com.teatreats.purchase.dto.PlaceOrderDTO;
import com.teatreats.purchase.service.CustomerOrderService;
import com.teatreats.purchase.utils.VerifyTokenAndReturnUserIdUtil;
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

  @Autowired private CustomerOrderService orderService;
  @Autowired private VerifyTokenAndReturnUserIdUtil verifyTokenAndReturnUserIdUtil;

  @PatchMapping("/status")
  public ResponseEntity<?> updateOrderStatus(
          @RequestBody UpdateOrderStatusDTO orderStatusDTO, HttpServletRequest request) {

    if (!verifyTokenAndReturnUserIdUtil.validateAdminToken(request)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    Optional<?> order = orderService.updateOrderStatus(orderStatusDTO.getOrderId(), orderStatusDTO.getStatus());
    return ResponseEntity.ok(order);
  }

  @GetMapping()
  public ResponseEntity<?> getAll(HttpServletRequest request) {
    if (!verifyTokenAndReturnUserIdUtil.validateAdminToken(request)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body("Access Forbidden to the Endpoint");
    }
    return ResponseEntity.ok(orderService.getAllOrder());
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<?> getByOrderId(HttpServletRequest request, @PathVariable int orderId) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getOrder(orderId, userId));
  }

  @GetMapping("/all")
  public ResponseEntity<?> getUserAllOrders(HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getUserAllOrder(userId));
  }

  @PostMapping()
  public ResponseEntity<?> placeOrder(
          @Valid @RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    Optional<?> response = orderService.placeOrder(
            Optional.ofNullable(placeOrderDTO.getCartItemList()),
            Optional.ofNullable(placeOrderDTO.getCartId()),
            userId,
            request.getHeader("Authorization").substring(7));
    return ResponseEntity.ok(response);
  }
}