package com.ripple.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private String itemName;
    private String category;
    private Integer quantity;
    private Double unitPrice;
}
