package com.ripple.order_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private Integer id;
    private String userId;
    private String restaurant;
    private String cuisineType;
    private Integer boostScore;
    private Double rating;
}
