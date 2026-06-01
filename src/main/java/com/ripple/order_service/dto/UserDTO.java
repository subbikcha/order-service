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

    private Integer rewardPoints;

    private String email;

    private String phoneNumber;

    private String address;

    private Double walletCredit;

    private Boolean isActive; // nullable: treat null as distinct state from true/false
}
