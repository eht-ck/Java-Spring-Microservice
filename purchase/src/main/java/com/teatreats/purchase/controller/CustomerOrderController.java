package com.teatreats.purchase.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;

import com.teatreats.purchase.dto.StripeResponse;
import com.teatreats.purchase.dto.ProductRequest;
import com.teatreats.purchase.dto.UpdateOrderStatusDTO;
import com.teatreats.purchase.dto.PlaceOrderDTO;
import com.teatreats.purchase.entity.PendingOrder;
import com.teatreats.purchase.repository.StripeRepository;
import com.teatreats.purchase.service.CustomerOrderService;
import com.teatreats.purchase.service.StripeService;
import com.teatreats.purchase.utils.VerifyTokenAndReturnUserIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/order/")
public class CustomerOrderController {

  @Autowired private CustomerOrderService orderService;
  @Autowired private VerifyTokenAndReturnUserIdUtil verifyTokenAndReturnUserIdUtil;

  @PatchMapping("/status")
  public ResponseEntity<?> updateOrderStatus(
      @RequestBody UpdateOrderStatusDTO orderStatusDTO, HttpServletRequest request) {

    if (!verifyTokenAndReturnUserIdUtil.validateAdminToken(request)) {
      log.error("Access forbidden to the endpoint!!");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Forbidden to the Endpoint");
    }
    Optional<?> order =
        orderService.updateOrderStatus(orderStatusDTO.getOrderId(), orderStatusDTO.getStatus());
    return ResponseEntity.ok(order);
  }

  @GetMapping()
  public ResponseEntity<?> getAll(HttpServletRequest request) {
    if (!verifyTokenAndReturnUserIdUtil.validateAdminToken(request)) {
      log.error("Access forbidden to the endpoint!!!");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Forbidden to the Endpoint");
    }
    return ResponseEntity.ok(orderService.getAllOrder());
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<?> getByOrderId(HttpServletRequest request, @PathVariable int orderId) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getOrder(orderId, userId));
  }

  @GetMapping("/all")
  public ResponseEntity<?> getUserAllOrders(HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getUserAllOrder(userId));
  }

  @PostMapping()
  public ResponseEntity<?> placeOrder(
      @Valid @RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    Optional<?> response =
        orderService.placeOrder(
            Optional.ofNullable(placeOrderDTO.getCartItemList()),
             userId,
            request.getHeader("Authorization").substring(7));
    return ResponseEntity.ok(response);
  }

  @Autowired private StripeService stripeService;

  @Value("${stripe.secretKey}")
  private String secretKey;
  @PostMapping("/stripeCheckout")
  public ResponseEntity<?> placeOrder(@RequestBody ProductRequest productRequest, String orderDataJson, HttpServletRequest request) {
    try {
      String userId = String.valueOf((verifyTokenAndReturnUserIdUtil.validateToken(request)));

      StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest, userId, orderDataJson, request.getHeader("Authorization").substring(7));
      return ResponseEntity.ok(stripeResponse);
    } catch (StripeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Stripe session: " + e.getMessage());
    }
  }
  @Autowired
  private StripeRepository stripeRepository;

  @PostMapping("/stripeWebhook")
  public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                    @RequestHeader("Stripe-Signature") String sigHeader) {
    try {
      Event event = Webhook.constructEvent(payload, sigHeader, secretKey);

      if ("checkout.session.completed".equals(event.getType())) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().get();
        String sessionId = session.getId();

        Optional<PendingOrder> pendingOrderOptional = stripeRepository.findById(sessionId);

        if (!pendingOrderOptional.isPresent()) {
          return ResponseEntity.badRequest().body("Order not found!");
        }

        PendingOrder pendingOrder = pendingOrderOptional.get();
        String orderDataJson = pendingOrder.getOrderData();
//        PlaceOrderDTO placeOrderDTO = objectMapper.readValue(orderDataJson, PlaceOrderDTO.class);

//        orderService.placeOrder(
//                Optional.ofNullable(placeOrderDTO.getCartItemList()),
//                Optional.ofNullable(placeOrderDTO.getCartId()),
//                Integer.parseInt(pendingOrder.getUserId()),
//                pendingOrder.getToken()
//        );

        pendingOrder.setStatus("COMPLETED");
        stripeRepository.save(pendingOrder);

        return ResponseEntity.ok("Order placed successfully!");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Webhook error: " + e.getMessage());
    }
    return ResponseEntity.ok("Unhandled event type");
  }
}
