package com.teatreats.purchase.controller;

import com.teatreats.purchase.dto.CartItemDTO;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.service.CartItemService;
import com.teatreats.purchase.service.CartService;
import com.teatreats.purchase.utils.VerifyTokenAndReturnUserIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/cart")
public class CartItemController {

  @Autowired private CartItemService cartItemService;
  @Autowired private CartService cartService;
  @Autowired private VerifyTokenAndReturnUserIdUtil verifyTokenAndReturnUserIdUtil;

  @PostMapping
  public ResponseEntity<?> createCartItem(
          @Valid @RequestBody CartItemDTO cartItemDTO, HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    CartItem createdCartItem = cartItemService.createCartItem(cartItemDTO, userId);
    return ResponseEntity.ok(createdCartItem);
  }

  @PatchMapping("/{cartItemId}/quantity/{quantity}")
  public ResponseEntity<?> updateCartItem(
          @PathVariable int cartItemId, @PathVariable int quantity, HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    Optional<?> updatedCartItem = cartItemService.updateCartItemQuantity(cartItemId, quantity, userId);
    return ResponseEntity.ok(updatedCartItem);
  }

  @DeleteMapping("/clearCart")
  public ResponseEntity<?> clearCart(HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    cartItemService.clearCart(userId);
    return ResponseEntity.noContent().build();
  }
}