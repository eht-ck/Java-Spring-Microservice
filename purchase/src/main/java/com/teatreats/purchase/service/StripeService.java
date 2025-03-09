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
    String sessionId = UUID.randomUUID().toString();
    PendingOrder pendingOrder = new PendingOrder();
    pendingOrder.setSessionId(sessionId);
    pendingOrder.setOrderData(orderDataJson);
    pendingOrder.setUserId(userId);
    pendingOrder.setToken(token);

    stripeRepository.save(pendingOrder);

    SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:8082/api/order/stripeSuccess?sessionId=" + sessionId)
            .setCancelUrl("http://localhost:8082/api/order/stripeCancel")
            .build();

    Session session = Session.create(params);

    return StripeResponse.builder()
            .status("SUCCESS")
            .message("Payment session created")
            .sessionId(session.getId())
            .sessionUrl(session.getUrl())
            .build();
  }
}
