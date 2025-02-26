package com.teatreats.purchase.service;

import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;
    public CartItem createCartItem(CartItem cartItem) {
        List<CartItem> cartItemList = cartItemRepository.findAllByCart_CartId(cartItem.getCart().getCartId());
        for (CartItem item : cartItemList) {
            if (item.getProductId() == cartItem.getProductId()) {
                item.setQuantity(item.getQuantity() + cartItem.getQuantity());
                return cartItemRepository.save(item);
            }
        }

        return cartItemRepository.save(cartItem);
    }


    public void deleteCartItem(int id) {
         cartItemRepository.deleteById(id);
    }

    public CartItem updateCartItemQuantity(int id, int quantity) {
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            return cartItem;
        } else {
             throw new RuntimeException("CartItem not found with id: " + id);
        }
    }
    @Transactional
    public void clearCart(int cartId) {
        cartItemRepository.deleteAllByCart_CartId(cartId);
    }


    public List<CartItem> getByCartID(int cartId) {
        return  cartItemRepository.findAllByCart_CartId(cartId);

    }
}
