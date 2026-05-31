package com.ripple.order_service.dto;

import lombok.*;
import java.util.Objects;

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

    private Double walletBalance;

    private Boolean isActive;

    public boolean isActiveOrDefault() {
        if (isActive == null) {
            // null isActive means status not yet determined; treating as active per migration strategy
            java.util.logging.Logger.getLogger(UserDTO.class.getName())
                .warning("isActive is null for userId: " + userId + "; treating as active.");
        }
        return Objects.requireNonNullElse(isActive, true);
    }
}
