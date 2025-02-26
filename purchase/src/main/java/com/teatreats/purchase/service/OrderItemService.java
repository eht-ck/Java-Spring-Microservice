package com.teatreats.purchase.service;

import com.teatreats.purchase.entity.OrderItem;
import com.teatreats.purchase.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;
    public List<OrderItem> getByOrderId(int orderId) {
        return  orderItemRepository.findAllByOrder_OrderId(orderId);
    }
}
