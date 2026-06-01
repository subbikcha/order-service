package com.ripple.order_service.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Setter
    private String userId;

    @Setter
    private String userName;

    @Setter
    private String tier;

    private Integer rewardPoints;

    @Setter
    private String email;

    @Setter
    private String phoneNumber;

    @Setter
    private String address;

    @Setter
    private Double walletBalance;

    @Setter
    private Boolean isActive;

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = (rewardPoints != null) ? rewardPoints / 10 : null;
    }
}
