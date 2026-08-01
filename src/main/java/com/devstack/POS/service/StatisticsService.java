package com.devstack.POS.service;

import com.devstack.POS.dto.response.StatisticsResponseDTO;

public interface StatisticsService {
    StatisticsResponseDTO getStatistics(Integer year);
}
