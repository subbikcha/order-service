package com.ripple.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private String userId;

    private String userName;

    private String tier;

    private Integer rewardPoints;
}
