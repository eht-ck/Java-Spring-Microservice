package com.teatreats.purchase.repository;

import com.teatreats.purchase.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface  CartItemRepository extends JpaRepository<CartItem, Integer> {
    @Modifying
    @Transactional
    void deleteAllByCart_CartId(int cartId);
    List<CartItem> findAllByCart_CartId(int id);

}
