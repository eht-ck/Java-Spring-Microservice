package com.teatreats.purchase.controller;

import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.service.CartService;
import com.teatreats.purchase.utils.VerifyTokenAndReturnUserIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart/")
public class CartController {

  @Autowired private CartService cartService;

  @Autowired private VerifyTokenAndReturnUserIdUtil verifyTokenAndReturnUserIdUtil;

  @GetMapping()
  public ResponseEntity<?> getCartByUserId(HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    Optional<Cart> cart = cartService.getCartByUserId(userId);
    if (cart.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
    }
    return ResponseEntity.ok(cart);
  }
}