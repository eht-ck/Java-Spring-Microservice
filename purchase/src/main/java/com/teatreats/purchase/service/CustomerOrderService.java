package com.teatreats.purchase.service;

import com.teatreats.purchase.dto.ProductDTO;
import com.teatreats.purchase.dto.UserDTO;
import com.teatreats.purchase.entity.*;
import com.teatreats.purchase.repository.CartItemRepository;
import com.teatreats.purchase.repository.CartRepository;
import com.teatreats.purchase.repository.CustomerOrderRepository;
import com.teatreats.purchase.repository.OrderItemRepository;
import io.jsonwebtoken.security.Jwks;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class CustomerOrderService {

  @Autowired private WebClient webClient;

  @Autowired private CustomerOrderRepository customerOrderRepository;

  @Autowired private CartItemRepository cartItemRepository;

  @Autowired private OrderItemRepository orderItemRepository;

  @Autowired private CartRepository cartRepository;

  @Transactional
  public Optional<?> placeOrder(Optional<List<Integer>> cartItemIDs, int userId, String token) {

    UserDTO userDTO =
        webClient
            .get()
            .uri("http://localhost:8080/api/user/id/" + userId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(UserDTO.class)
            .block();

    Set<CartItem> outOfStockProducts = new HashSet<>();
    Double totalAmount = 0.0;
    Map<Integer, ProductDTO> productMap = new HashMap<>();
    List<CartItem> cartItemList;

    if (cartItemIDs.isPresent()) {
      List<Integer> cartItemIds = cartItemIDs.get();
      cartItemList = cartItemRepository.findAllById(cartItemIds);
      System.out.println("CHECK LIST OF CART ITEM" + cartItemList);
    } else {
      Optional<Cart> cartId = cartRepository.findByUserId(userId);
      cartItemList = cartItemRepository.findAllByCart_CartId(cartId.get().getCartId());
    }


    if (cartItemList.isEmpty()) {
      return Optional.of("No item found");
    }


    for (CartItem cartItem : cartItemList) {
      try {
        Mono<ProductDTO> productDTOMono =
            webClient
                .get()
                .uri("http://localhost:8081/api/products/" + cartItem.getProductId())
                .retrieve()
                .bodyToMono(ProductDTO.class);
        ProductDTO productDTO = productDTOMono.block();
        productMap.put(cartItem.getProductId(), productDTO);

        if (productDTO.getStockQuantity() < cartItem.getQuantity()) {
          outOfStockProducts.add(cartItem);
        }

        totalAmount +=
            cartItem.getQuantity()
                * (productDTO.getPrice()
                    - productDTO.getPrice() * (double) cartItem.getDiscount() / 100);

      } catch (Exception e) {
        log.error("Error fetching product details for product ID: " + cartItem.getProductId(), e);
        return Optional.of("Error fetching product details");
      }
    }

    if (!outOfStockProducts.isEmpty()) {
      log.warn("Out of stock products: " + outOfStockProducts);
      List<Integer> outOfStockProductIDs = new ArrayList<>();
      for (CartItem item : outOfStockProducts) {
        outOfStockProductIDs.add(item.getProductId());
      }
      return Optional.of("Some products are out of stock" + outOfStockProductIDs);
    }

    CustomerOrder order = new CustomerOrder();
    order.setUserId(userId);
    order.setTotalAmount(totalAmount);
    order.setStatus(Status.PENDING);
    order.setDeliveryAddress(userDTO.getAddress());

    CustomerOrder customerOrder;
    try {
      customerOrder = customerOrderRepository.save(order);
    } catch (Exception e) {
      log.error("Error saving customer order", e);
      return Optional.of("Error placing order");
    }

    List<OrderItem> orderItemList = new ArrayList<>();
    List<Integer> cartItemIdList = new ArrayList<>();

    for (CartItem cartItem : cartItemList) {
      OrderItem orderItem = new OrderItem();
      orderItem.setProductId(cartItem.getProductId());
      orderItem.setQuantity(cartItem.getQuantity());
      orderItem.setPrice(productMap.get(cartItem.getProductId()).getPrice());
      orderItem.setOrder(customerOrder);
      orderItem.setDiscount(cartItem.getDiscount());
      orderItem.setProductName(productMap.get(cartItem.getProductId()).getName());
      orderItem.setProductDescription(productMap.get(cartItem.getProductId()).getDescription());
      orderItemList.add(orderItem);
      cartItemIdList.add(cartItem.getCartItemId());

      try {
        System.out.println("Cart item: " + cartItem.getQuantity());
        Mono<ProductDTO> productDTOMono =
            webClient
                .patch()
                .uri("http://localhost:8081/api/products/updateQuantity/" + cartItem.getProductId())
                .bodyValue(cartItem.getQuantity())
                .retrieve()
                .bodyToMono(ProductDTO.class);
        productDTOMono.block();
      } catch (Exception e) {
        log.error("Error updating product quantity for product ID: " + cartItem.getProductId(), e);
        return Optional.of("Error updating product quantity");
      }
    }

    try {
      orderItemRepository.saveAll(orderItemList);
      cartItemRepository.deleteAllById(cartItemIdList);
    } catch (Exception e) {
      log.error("Error saving order items or deleting cart items", e);
      return Optional.of("Error finalizing order");
    }

    return Optional.of("ORDER PLACED!!!!!");
  }

  public Optional<?> updateOrderStatus(int orderId, Status status) {
    Optional<CustomerOrder> order = customerOrderRepository.findById(orderId);
    if (order.isPresent()) {
      CustomerOrder customerOrder = order.get();
      customerOrder.setStatus(status);
      customerOrderRepository.save(customerOrder);

      // If the status is CANCELLED, handle the cancellation logic
      if (status == Status.CANCELLED) {
        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder_OrderId(orderId);
        for (OrderItem orderItem : orderItemList) {
          webClient
              .patch()
              .uri(
                  "http://localhost:8081/api/products/increaseQuantity/" + orderItem.getProductId())
              .bodyValue(orderItem.getQuantity())
              .retrieve()
              .bodyToMono(ProductDTO.class)
              .block();
        }
      }
    } else {
      return Optional.of("order not found");
    }
    return order;
  }

  public List<CustomerOrder> getAllOrder() {
    return customerOrderRepository.findAll();
  }

  public Optional<?> getOrder(int orderId, int userId) {
    Optional<CustomerOrder> order = customerOrderRepository.findByOrderIdAndUserId(orderId, userId);
    if (order.isPresent()) {
      return order;
    }
    return Optional.of("No order found for you with order id " + orderId);
  }

  public List<CustomerOrder> getUserAllOrder(int userId) {
    return customerOrderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "orderDate"));  }
}
