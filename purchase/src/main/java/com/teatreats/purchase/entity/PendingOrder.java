package com.teatreats.purchase.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PendingOrder {
    @Id
  private String sessionId;
  private String userId;
  private String orderData;

  private  String token;

  private  String status;
    }
