package com.devstack.POS.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MonthlyDataResponseDTO {
    private String month;
    private int monthValue;
    private double income;
    private long orderCount;
}
