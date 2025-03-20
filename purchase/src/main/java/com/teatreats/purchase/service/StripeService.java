package com.teatreats.purchase.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.tax.Registration;
import com.stripe.param.checkout.SessionCreateParams;
import com.teatreats.purchase.dto.ProductRequest;
import com.teatreats.purchase.dto.StripeResponse;
import com.teatreats.purchase.entity.PendingOrder;
import com.teatreats.purchase.repository.StripeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StripeService {
  @Autowired
  private StripeRepository stripeRepository;

  @Value("${stripe.secretKey}")
  private String secretKey;

  public StripeResponse checkoutProducts(ProductRequest productRequest, String userId, String orderDataJson, String token) throws StripeException {
    Stripe.apiKey = secretKey;

    SessionCreateParams.LineItem.PriceData.ProductData productData =
        SessionCreateParams.LineItem.PriceData.ProductData.builder()
            .setName(productRequest.getName())
            .build();

     SessionCreateParams.LineItem.PriceData priceData =
        SessionCreateParams.LineItem.PriceData.builder()
            .setCurrency(
                productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
            .setUnitAmount(productRequest.getAmount())
            .setProductData(productData)
            .build();

     SessionCreateParams.LineItem lineItem =
        SessionCreateParams.LineItem.builder()
            .setQuantity(productRequest.getQuantity())
            .setPriceData(priceData)
            .build();
    SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:3000/orderplaced")
            .setCancelUrl("http://localhost:8082/api/order/stripeCancel")
            .addLineItem(lineItem)
            .build();

    // builder pattern -> lombok

    Session session = Session.create(params);
     String sessionId = session.getId();
    PendingOrder pendingOrder = new PendingOrder();
    pendingOrder.setSessionId(sessionId);
    pendingOrder.setOrderData(orderDataJson);
    pendingOrder.setUserId(userId);
    pendingOrder.setToken(token);

    stripeRepository.save(pendingOrder);
    return StripeResponse.builder()
            .status("SUCCESS")
            .message("Payment session created")
            .sessionId(session.getId())
            .sessionUrl(session.getUrl())
            .build();
  }
}
