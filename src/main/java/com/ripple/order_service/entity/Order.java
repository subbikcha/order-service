package com.ripple.order_service.entity;

import com.ripple.order_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    private String userId;

    private String userName;

    private String tier;

    private Integer totalAmount;

    private Integer deliveryFee;

    @Builder.Default
    private Integer discountAmount = 0;

    @Builder.Default
    private Integer walletDiscount = 0;

    private Integer finalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String restaurantName;

    private String deliveryAddress;

    private String notes;

    // Carried from user-service at order time
    private String customerEmail;

    private String contactPhone;

    // Carried from recommendation-service at order time
    private String recommendedRestaurantHint;

    private Boolean matchedRecommendation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private List<OrderItem> items;
}
