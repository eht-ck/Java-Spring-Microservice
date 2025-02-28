package com.teatreats.purchase.dto;

import com.teatreats.purchase.entity.Status;
import lombok.Data;

@Data
public class UpdateOrderStatusDTO {

    private Status status;

    private int orderId;
}
