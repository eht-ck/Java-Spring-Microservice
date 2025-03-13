package com.teatreats.purchase.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;
import com.teatreats.purchase.dto.*;
import com.teatreats.purchase.entity.CustomerOrder;
import com.teatreats.purchase.entity.PendingOrder;
import com.teatreats.purchase.repository.StripeRepository;
import com.teatreats.purchase.service.CustomerOrderService;
import com.teatreats.purchase.service.StripeService;
import com.teatreats.purchase.utils.VerifyTokenAndReturnUserIdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/order/")
public class CustomerOrderController {
  @Autowired private StripeService stripeService;

  @Value("${stripe.secretKey}")
  private String secretKey;

  @Autowired private StripeRepository stripeRepository;
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
  public ResponseEntity<?> getUserAllOrders(HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getUserAllOrder(userId));
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<?> getByOrderId(HttpServletRequest request, @PathVariable int orderId) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return ResponseEntity.ok(orderService.getOrder(orderId, userId));
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAll(
      HttpServletRequest request,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    if (!verifyTokenAndReturnUserIdUtil.validateAdminToken(request)) {
      log.error("Access forbidden to the endpoint!!!");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Access Forbidden to the Endpoint");
    }

    Page<CustomerOrder> orders =
        orderService.getAllOrders(PageRequest.of(page, size, Sort.by("orderDate").descending()));
    return ResponseEntity.ok(orders);
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

  @PostMapping("/stripeCheckout")
  public ResponseEntity<?> placeOrder(
      @RequestBody ProductRequest productRequest, HttpServletRequest request) {
    try {
      String userId = String.valueOf((verifyTokenAndReturnUserIdUtil.validateToken(request)));
      System.out.println(productRequest);
      StripeResponse stripeResponse =
          stripeService.checkoutProducts(
              productRequest,
              userId,
              productRequest.getOrderDataJson(),
              request.getHeader("Authorization").substring(7));
      return ResponseEntity.ok(stripeResponse);
    } catch (StripeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error creating Stripe session: " + e.getMessage());
    }
  }

  @PostMapping("/stockCheck")
  public Optional<String> checkStockItem(
          @RequestBody PlaceOrderDTO placeOrderDTO, HttpServletRequest request) {
    int userId = verifyTokenAndReturnUserIdUtil.validateToken(request);
    return orderService.checkCartItems(Optional.ofNullable(placeOrderDTO.getCartItemList()), userId);
  }
  @PostMapping("/stripeWebhook")
  public ResponseEntity<String> handleStripeWebhook(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
    System.out.println("here");
    try {
      Event event =
          Webhook.constructEvent(payload, sigHeader, "whsec_fG1TRw95AH85U5qtaTcHeymDZps3K8VG");
      if ("checkout.session.completed".equals(event.getType())) {
        Session session = (Session) event.getDataObjectDeserializer().getObject().get();
        String sessionId = session.getId();

        System.out.println("Session ID: " + sessionId);
        Optional<PendingOrder> pendingOrderOptional = stripeRepository.findById(sessionId);

        if (!pendingOrderOptional.isPresent()) {
          System.out.println("NO ORDER PRESENT");
          return ResponseEntity.badRequest().body("Order not found!");
        }

        PendingOrder pendingOrder = pendingOrderOptional.get();
        String orderDataJson = pendingOrder.getOrderData();
        System.out.println(orderDataJson);
        JSONObject jsonObject = new JSONObject(orderDataJson);

        JSONArray jsonArray = jsonObject.getJSONArray("cartItemList");
        List<Integer> cartItemList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
          cartItemList.add(jsonArray.getInt(i));
        }
        System.out.println(cartItemList);
        orderService.placeOrder(
            Optional.ofNullable(cartItemList),
            Integer.parseInt(pendingOrder.getUserId()),
            pendingOrder.getToken());
        System.out.println("order placed");

        pendingOrder.setStatus("COMPLETED");
        stripeRepository.save(pendingOrder);
      }

      return ResponseEntity.ok("Order placed successfully!");
    } catch (SignatureVerificationException ex) {
      throw new RuntimeException(ex);
    }
  }
}
