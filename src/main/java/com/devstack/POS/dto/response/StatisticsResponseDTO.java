package com.devstack.POS.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StatisticsResponseDTO {
    private double totalIncome;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private int year;
    private List<Integer> availableYears;
    private List<MonthlyDataResponseDTO> monthlyData;
}
