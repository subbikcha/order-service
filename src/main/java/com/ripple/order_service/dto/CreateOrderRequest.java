package com.ripple.order_service.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String deliveryAddress;
    private String restaurantName;
    private String notes;
    private List<OrderItemRequest> items;
}
