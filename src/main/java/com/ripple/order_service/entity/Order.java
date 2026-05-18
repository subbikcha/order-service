package com.ripple.order_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    private String userId;

    private String userName;

    private String tier;

    private Integer totalAmount;

    private Integer deliveryFee;
}