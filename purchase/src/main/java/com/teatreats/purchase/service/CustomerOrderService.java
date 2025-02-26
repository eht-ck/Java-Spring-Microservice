package com.teatreats.purchase.service;

import com.teatreats.purchase.dto.ProductDTO;
import com.teatreats.purchase.entity.CartItem;
import com.teatreats.purchase.entity.CustomerOrder;
import com.teatreats.purchase.entity.OrderItem;
import com.teatreats.purchase.entity.Status;
import com.teatreats.purchase.repository.CartItemRepository;
import com.teatreats.purchase.repository.CustomerOrderRepository;
import com.teatreats.purchase.repository.OrderItemRepository;
import io.jsonwebtoken.security.Jwks;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Transactional
  public Optional<?> placeOrder(List<CartItem> cartItemList, int userId, String address) {
    Set<CartItem> outOfStockProducts = new HashSet<>();
    Double totalAmount = 0.0;
    Map<Integer, ProductDTO> productMap = new HashMap<>();

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

        if(cartItemRepository.findById(cartItem.getCartItemId()).isEmpty()){
          log.error("CartItem does not exist in database");

          return Optional.of("ID NHI MILI ");
        }

        totalAmount +=
              cartItem.getQuantity() *  (productDTO.getPrice() - productDTO.getPrice() * (double) cartItem.getDiscount() / 100);

        System.out.println("TOTAL PRICE" + totalAmount);
      } catch (Exception e) {
        log.error("Error fetching product details for product ID: " + cartItem.getProductId(), e);
        return Optional.of("Error fetching product details");
      }
    }

    if (!outOfStockProducts.isEmpty()) {
      log.warn("Out of stock products: " + outOfStockProducts);
      return Optional.of("Some products are out of stock" + outOfStockProducts);
    }

    CustomerOrder order = new CustomerOrder();
    order.setUserId(userId);
    order.setTotalAmount(totalAmount);
    order.setStatus(Status.PENDING);
    order.setDeliveryAddress(address);

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

  public Optional<?> cancelOrder(int orderId) {
    // CANCEL ORDER
    Optional<CustomerOrder> order = customerOrderRepository.findById(orderId);
    if(order.get().getStatus() == Status.CANCELLED){
      return Optional.of("Already Cancelled!!");
    }
    order.get().setStatus(Status.CANCELLED);
    customerOrderRepository.save(order.get());

    List<OrderItem> orderItemList = orderItemRepository.findAllByOrder_OrderId(orderId);
    for (OrderItem orderItem : orderItemList) {

      webClient
          .patch()
          .uri("http://localhost:8081/api/products/increaseQuantity/" + orderItem.getProductId())
          .bodyValue(orderItem.getQuantity())
          .retrieve()
          .bodyToMono(ProductDTO.class);
    }
    return order;
  }

  public List<CustomerOrder> getAllOrder() {

    return  customerOrderRepository.findAll();
  }

  public Optional<?> updateOrderStatus(int orderId, Status status) {
    Optional<CustomerOrder> order = customerOrderRepository.findById(orderId);
    if (order.isPresent()) {
      CustomerOrder customerOrder = order.get();
      customerOrder.setStatus(status);
      customerOrderRepository.save(customerOrder);
    }
    return order;
  }
}
