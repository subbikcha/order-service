package com.ripple.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userId;

    private String userName;

    private String tier;

    private Integer loyaltyPoints;

    private String email;

    private String phoneNumber;

    private String address;

    private Double walletBalance; // value in paise; divide by 100 to convert to rupees

    private Boolean isActive;
}
