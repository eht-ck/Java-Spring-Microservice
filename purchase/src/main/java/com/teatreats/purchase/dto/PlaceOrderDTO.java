package com.teatreats.purchase.dto;

import com.teatreats.purchase.entity.Cart;
import com.teatreats.purchase.entity.CartItem;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderDTO {

  private List<Integer> cartItemList;

 }
