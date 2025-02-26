package com.teatreats.purchase.controller;

import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/api/cart/")
public class CartController {

    @Autowired
    private CartService cartService;
    @PostMapping
    public ResponseEntity<Optional<Cart>> createCart(@Valid @RequestBody Cart cart) {
        Optional<Cart> createdCart = cartService.createCart(cart);
        return ResponseEntity.ok(createdCart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Cart>> getCartById(@PathVariable int id) {
        Optional<Cart> cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/user/{userId}")

    public ResponseEntity<Optional<Cart>> getCartByUserId(@PathVariable int userId){
        Optional<Cart> cart = cartService.getCartByUserId(userId);
        return  ResponseEntity.ok(cart);
    }


}
