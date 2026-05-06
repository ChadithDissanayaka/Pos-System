package com.devstack.POS.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CustomerOrderResponseDTO {
    private UUID orderId;
    private UUID customerId;
    private String customerName;
    private double totalCost;
    private LocalDate date;
    private List<OrderDetailsResponseDTO> details;
}
