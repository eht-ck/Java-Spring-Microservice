package com.teatreats.purchase.controller;

import com.teatreats.purchase.entity.OrderItem;
import com.teatreats.purchase.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order/orderItem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;


    @GetMapping("/{orderId}")
    public List<OrderItem> getByOrderId(@PathVariable int orderId){
        List<OrderItem> orderItemList = orderItemService.getByOrderId(orderId);
        return  orderItemList;
    }
}
