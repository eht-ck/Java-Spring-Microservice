package com.teatreats.purchase.service;

import com.teatreats.purchase.dto.CartItemDTO;
import com.teatreats.purchase.dto.ProductDTO;
import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.repository.CartItemRepository;
import com.teatreats.purchase.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class CartItemService {

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private CartRepository cartRepository;

  @Autowired private WebClient webClient;

  public CartItem createCartItem(CartItemDTO cartItemDTO, int userId) {
    Optional<Cart> cart = cartRepository.findByUserId(userId);
    CartItem cartItem =
        cartItemRepository.findByProductIdAndCart_CartId(
            cartItemDTO.getProductId(), cart.get().getCartId());
    if (cartItem != null) {
      cartItem.setQuantity(cartItem.getQuantity() + cartItemDTO.getQuantity());
      return cartItemRepository.save(cartItem);
    }

    float discount = 0.0F;

    Mono<ProductDTO> productDTOMono =
        webClient
            .get()
            .uri("http://localhost:8081/api/products/" + cartItemDTO.getProductId())
            .retrieve()
            .bodyToMono(ProductDTO.class);

    ProductDTO productDTO = productDTOMono.block();
    String category = productDTO.getCategory();
    System.out.println(category);
    if (category.equals("GIFT_SETS")) {
      discount = 10;
    }
    CartItem cartItem1 = new CartItem();
    cartItem1.setDiscount(discount);
    cartItem1.setQuantity(cartItemDTO.getQuantity());
    cartItem1.setProductId(cartItemDTO.getProductId());
    cartItem1.setCart(cart.get());
    return cartItemRepository.save(cartItem1);
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
          log.warn( "Unauthorized: Cart does not belong to the user with id: " + userId);
          throw new RuntimeException(
              "Unauthorized: Cart does not belong to the user with id: " + userId);
        }
      } else {
        log.warn("Cart not found with id: " + cartItem.getCart().getCartId());
        throw new RuntimeException("Cart not found with id: " + cartItem.getCart().getCartId());
      }
    } else {
      log.warn("CartItem not found with id: " + id);
      throw new RuntimeException("CartItem not found with id: " + id);
    }
  }

  public Optional<?> updateCartItemQuantity(int cartItemId, int quantity, int userId) {

      Optional<CartItem> cartItem =
          cartItemRepository.findById(cartItemId);
      System.out.println("CartItem: " + cartItem);
      if (cartItem.isPresent()) {
        if (quantity == 0) {
          System.out.println("Quantity is 0, deleting cart item");
          cartItemRepository.deleteById(cartItem.get().getCartItemId());
          return Optional.of("DELETED");
        }
        cartItem.get().setQuantity(quantity);
        System.out.println("Updated CartItem: " + cartItem.get());
        return Optional.of(cartItemRepository.save(cartItem.get()));
      }
      else {
        Optional.of("Cart not found!!");
      }
    return Optional.of("CART NOT FOUND");

  }

  @Transactional
  public void clearCart(int userId) {
    System.out.println(userId);
    Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
    System.out.println(cartOptional);
    if (cartOptional.isPresent()) {
      Cart cart = cartOptional.get();
        cartItemRepository.deleteAllByCart_CartId(cart.getCartId());
      }else {
        log.warn("Cart does not belong to the user with id" + userId);
        throw new RuntimeException(
            "Unauthorized: Cart does not belong to the user with id: " + userId);
      }
    }



  public Optional<CartItem> getCartItem(int cartItemId) {
    return cartItemRepository.findById(cartItemId);
  }
}
