package com.teatreats.purchase.controller;

import com.teatreats.purchase.dto.UpdateCartItemDTO;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartItem")
public class CartItemController {
  @Autowired private CartItemService cartItemService;

  @PostMapping
  public ResponseEntity<CartItem> createCartItem(@Valid @RequestBody CartItem cartItem) {
    cartItem.setInCart(true);
    CartItem createdCartItem = cartItemService.createCartItem(cartItem);
    return ResponseEntity.ok(createdCartItem);
  }

  @PatchMapping("/updateQuantity")
  public ResponseEntity<CartItem> updateCartItem(
      @Valid @RequestBody UpdateCartItemDTO updateCartItemDTO) {
    CartItem updatedCartItem =
        cartItemService.updateCartItemQuantity(
            updateCartItemDTO.getCartItemId(), updateCartItemDTO.getQuantity());
    return ResponseEntity.ok(updatedCartItem);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCartItem(@PathVariable int id) {
    cartItemService.deleteCartItem(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/clearCart/{cartId}")
  public ResponseEntity<Void> clearCart(@PathVariable int cartId) {
    cartItemService.clearCart(cartId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/cartItemList/{cartId}")
  public ResponseEntity<List<CartItem>> cartItems(@PathVariable int cartId) {
    List<CartItem> cartItems = cartItemService.getByCartID(cartId);
    return ResponseEntity.ok(cartItems);
  }


}
