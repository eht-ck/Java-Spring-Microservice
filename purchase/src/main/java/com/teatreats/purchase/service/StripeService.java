package com.teatreats.purchase.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.tax.Registration;
import com.stripe.param.checkout.SessionCreateParams;
import com.teatreats.purchase.dto.ProductRequest;
import com.teatreats.purchase.dto.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

  @Value("${stripe.secretKey}")
  private String secretKey;

  // stripe -API
  // -> productName , amount , quantity , currency
  // -> return sessionId and url

  public StripeResponse checkoutProducts(ProductRequest productRequest) throws StripeException {
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

    SessionCreateParams params =
        SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl("http://localhost:8080/success")
            .setCancelUrl("http://localhost:8080/cancel")
            .addLineItem(lineItem)
            .build();

    Session session = Session.create(params);



    return StripeResponse.builder()
        .status("SUCCESS")
        .message("Payment session created ")
        .sessionId(session.getId())
        .sessionUrl(session.getUrl())
        .build();
  }
}
