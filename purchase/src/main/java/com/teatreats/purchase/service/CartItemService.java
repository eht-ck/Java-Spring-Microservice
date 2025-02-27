package com.teatreats.purchase.service;

import com.teatreats.purchase.dto.ProductDTO;
import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.repository.CartItemRepository;
import com.teatreats.purchase.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService {

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private WebClient webClient;

  public CartItem createCartItem(CartItem cartItem) {
// TODO: FIND ALL BY PRODUCTID -> THEN CONDITIONAL
    List<CartItem> cartItemList =
        cartItemRepository.findAllByCart_CartId(cartItem.getCart().getCartId());
    for (CartItem item : cartItemList) {
      if (item.getProductId() == cartItem.getProductId()) {
        item.setQuantity(item.getQuantity() + cartItem.getQuantity());
        return cartItemRepository.save(item);
      }
    }
    float discount = 0.0F;

    Mono<ProductDTO> productDTOMono =
        webClient
            .get()
            .uri("http://localhost:8081/api/products/" + cartItem.getProductId())
            .retrieve()
            .bodyToMono(ProductDTO.class);

    ProductDTO productDTO = productDTOMono.block();
    String category = productDTO.getCategory();
    System.out.println(category);
    if (category.equals("GIFT_SETS")) {
      discount = 10;
    }
    cartItem.setDiscount(discount);
    return cartItemRepository.save(cartItem);
  }

  public void deleteCartItem(int id, int userId) {
    Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
    if (cartItemOptional.isPresent()) {
      CartItem cartItem = cartItemOptional.get();
      Optional<Cart> cartOptional = cartRepository.findById(cartItem.getCart().getCartId());
      if (cartOptional.isPresent()) {
        int cartUserId = cartOptional.get().getUserId();
        if (userId == cartUserId) {
          cartItemRepository.deleteById(id);
        } else {
          throw new RuntimeException("Unauthorized: Cart does not belong to the user with id: " + userId);
        }
      } else {
        throw new RuntimeException("Cart not found with id: " + cartItem.getCart().getCartId());
      }
    } else {
      throw new RuntimeException("CartItem not found with id: " + id);
    }
  }

  public CartItem updateCartItemQuantity(int id, int quantity, int userId) {
    Optional<CartItem> cartItemOptional = cartItemRepository.findById(id);
    if (cartItemOptional.isPresent()) {
      CartItem cartItem = cartItemOptional.get();
      Optional<Cart> cartOptional = cartRepository.findById(cartItem.getCart().getCartId());
      if (cartOptional.isPresent()) {
        int cartUserId = cartOptional.get().getUserId();
        if (userId == cartUserId) {
          cartItem.setQuantity(quantity);
          cartItemRepository.save(cartItem);
          return cartItem;
        } else {
          throw new RuntimeException("Unauthorized: Cart does not belong to the user with id: " + userId);
        }
      } else {
        throw new RuntimeException("Cart not found with id: " + cartItem.getCart().getCartId());
      }
    } else {
      throw new RuntimeException("CartItem not found with id: " + id);
    }
  }

  @Transactional
  public void clearCart(int cartId, int userId) {
    Optional<Cart> cartOptional = cartRepository.findById(cartId);

    if (cartOptional.isPresent()) {
      Cart cart = cartOptional.get();
      int cartUserId = cart.getUserId();

      if (cartUserId == userId) {
        cartItemRepository.deleteAllByCart_CartId(cartId);
      } else {
        throw new RuntimeException("Unauthorized: Cart does not belong to the user with id: " + userId);
      }
    } else {
      throw new RuntimeException("Cart not found with id: " + cartId);
    }
  }
// remove
  public List<CartItem> getByCartID(int cartId, int userId) {

      Optional<Cart> cartOptional = cartRepository.findById(cartId);
      if (cartOptional.isPresent()) {
        Cart cart = cartOptional.get();
        int cartUserId = cart.getUserId();
        if (cartUserId == userId) {
          return cartItemRepository.findAllByCart_CartId(cartId);
        } else {
          throw new RuntimeException("Unauthorized: Cart does not belong to the user with id: " + userId);
        }
      } else {
        throw new RuntimeException("Cart not found with id: " + cartId);
      }


  }

  public Optional<CartItem> getCartItem(int cartItemId) {
    return cartItemRepository.findById(cartItemId);
  }
}
