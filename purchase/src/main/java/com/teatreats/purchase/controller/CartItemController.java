package com.teatreats.purchase.controller;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.teatreats.purchase.dto.UpdateCartItemDTO;
import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.service.CartItemService;
import com.teatreats.purchase.service.CartService;
import com.teatreats.purchase.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cartItem")
//  TODO:  cartItem -> CART
public class CartItemController {
  @Autowired private CartItemService cartItemService;
  @Autowired private CartService cartService;

  @Autowired private JWTService jwtService;

  @PostMapping
  public ResponseEntity<?> createCartItem(
      @Valid @RequestBody CartItem cartItem, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);

    int cartID = cartItem.getCart().getCartId();
    int userId = cartService.getCartById(cartID).get().getUserId();
    System.out.println(userId);
    if (jwtService.validateAllToken(token, userId)) {
      try {
        CartItem createdCartItem = cartItemService.createCartItem(cartItem);
        return ResponseEntity.ok(createdCartItem);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Product not found with the given product id");
      }
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("CartID does not correspond to valid userID");
  }
// CART/PRDOUCTid/QUANTITY
  // if quanitty is 0 -> delete from carlineitme
  @PatchMapping("/updateQuantity")
  public ResponseEntity<?> updateCartItem(
      @Valid @RequestBody UpdateCartItemDTO updateCartItemDTO, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);
    if (jwtService.validateAllToken(token, updateCartItemDTO.getUserId())) {
      try {
        //        System.out.println("HERE");
        CartItem updatedCartItem =
            cartItemService.updateCartItemQuantity(
                updateCartItemDTO.getCartItemId(),
                updateCartItemDTO.getQuantity(),
                updateCartItemDTO.getUserId());
        return ResponseEntity.ok(updatedCartItem);
      } catch (Exception e) {
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                "Unauthorized: Cart does not belong to the user with id: "
                    + updateCartItemDTO.getUserId());
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized for the user");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCartItem(@PathVariable int id, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);

    int userId = jwtService.validateAndGetUserId(token);
    if (userId != 0) {
      try {
        cartItemService.deleteCartItem(id, userId);
        return ResponseEntity.noContent().build();
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized for the user");
  }
// capi/cart/clearcart
  @DeleteMapping("/clearCart/{cartId}")
  public ResponseEntity<?> clearCart(@PathVariable int cartId, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);

    int userId = jwtService.validateAndGetUserId(token);
    if (userId != 0) {
    try{
      cartItemService.clearCart(cartId, userId);
      return ResponseEntity.noContent().build();}
    catch (Exception e){
      return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("USER NOT AUTHORIZED");
  }

  @GetMapping("/cartItemList/{cartId}")
  public ResponseEntity<?> cartItems(@PathVariable int cartId, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Missing or invalid Authorization header");
    }
    String token = authHeader.substring(7);

    int userId = jwtService.validateAndGetUserId(token);
    if (userId != 0) {
      try {
        List<CartItem> cartItems = cartItemService.getByCartID(cartId,userId);
        return ResponseEntity.ok(cartItems);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not authorized");
  }
}
