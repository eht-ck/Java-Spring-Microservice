package com.teatreats.purchase.repository;

import com.teatreats.purchase.entity.PendingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeRepository extends JpaRepository<PendingOrder, String> {

}
