package com.teatreats.purchase.repository;

import com.teatreats.purchase.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {


    Optional<CustomerOrder> findByOrderIdAndUserId(int orderId, int userId);


    List<CustomerOrder> findByUserId(int userId);
}


