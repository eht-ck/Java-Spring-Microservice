package com.teatreats.purchase.service;

import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {


    @Autowired
    private  CartRepository cartRepository;


    public Optional<Cart> createCart(Cart cart) {
        Optional<Cart> existingCart = cartRepository.findByUserId(cart.getUserId());
        if (existingCart.isPresent()) {
            return existingCart;
        }
        return Optional.of(cartRepository.save(cart));
    }

    public  Optional<Cart> getCartById(int id){
        return cartRepository.findById(id);

    }

    public  Optional<Cart> getCartByUserId(int id){
        return cartRepository.findByUserId(id);
    }


}

