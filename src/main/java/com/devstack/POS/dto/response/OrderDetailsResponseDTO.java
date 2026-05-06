package com.devstack.POS.dto.response;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class OrderDetailsResponseDTO {
    private UUID orderDetailsId;
    private UUID productId;
    private String productName;
    private double unitPrice;
    private int qty;
    private double total;
}
