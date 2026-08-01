package com.devstack.POS.service.impl;

import com.devstack.POS.dto.response.MonthlyDataResponseDTO;
import com.devstack.POS.dto.response.StatisticsResponseDTO;
import com.devstack.POS.entity.CustomerOrder;
import com.devstack.POS.repo.CustomerRepo;
import com.devstack.POS.repo.OrderRepo;
import com.devstack.POS.repo.ProductRepo;
import com.devstack.POS.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepo orderRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;

    @Override
    public StatisticsResponseDTO getStatistics(Integer year) {
        List<Integer> availableYears = orderRepo.findDistinctYears();
        if (availableYears == null) {
            availableYears = new ArrayList<>();
        }

        int targetYear;
        if (year != null) {
            targetYear = year;
        } else if (!availableYears.isEmpty()) {
            targetYear = availableYears.get(0);
        } else {
            targetYear = LocalDate.now().getYear();
        }

        if (!availableYears.contains(targetYear)) {
            availableYears.add(targetYear);
            availableYears.sort((a, b) -> Integer.compare(b, a));
        }

        Double totalIncomeDouble = orderRepo.findTotalIncome();
        double totalIncome = (totalIncomeDouble != null) ? totalIncomeDouble : 0.0;
        long totalOrders = orderRepo.count();
        long totalCustomers = customerRepo.count();
        long totalProducts = productRepo.count();

        // Prepare 12 months array
        List<MonthlyDataResponseDTO> monthlyData = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Month monthEnum = Month.of(i);
            String monthName = monthEnum.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyData.add(MonthlyDataResponseDTO.builder()
                    .month(monthName)
                    .monthValue(i)
                    .income(0.0)
                    .orderCount(0)
                    .build());
        }

        // Fetch orders for target year
        LocalDate startDate = LocalDate.of(targetYear, 1, 1);
        LocalDate endDate = LocalDate.of(targetYear, 12, 31);
        List<CustomerOrder> yearOrders = orderRepo.findByDateBetween(startDate, endDate);

        if (yearOrders != null) {
            for (CustomerOrder order : yearOrders) {
                if (order.getDate() != null) {
                    int mIdx = order.getDate().getMonthValue() - 1;
                    if (mIdx >= 0 && mIdx < 12) {
                        MonthlyDataResponseDTO current = monthlyData.get(mIdx);
                        current.setIncome(current.getIncome() + order.getTotalCost());
                        current.setOrderCount(current.getOrderCount() + 1);
                    }
                }
            }
        }

        return StatisticsResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .year(targetYear)
                .availableYears(availableYears)
                .monthlyData(monthlyData)
                .build();
    }
}
