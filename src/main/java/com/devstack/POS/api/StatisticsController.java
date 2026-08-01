package com.devstack.POS.api;

import com.devstack.POS.config.OpenApiConfig;
import com.devstack.POS.dto.response.StatisticsResponseDTO;
import com.devstack.POS.service.StatisticsService;
import com.devstack.POS.util.StandardResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "System statistics and business overview endpoints")
@SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @Operation(summary = "Get business statistics and monthly performance overview")
    public ResponseEntity<StandardResponseDTO> getStatistics(@RequestParam(required = false) Integer year) {
        StatisticsResponseDTO statistics = statisticsService.getStatistics(year);
        return ResponseEntity.ok(
                StandardResponseDTO.builder()
                        .code(200)
                        .message("Statistics retrieved successfully")
                        .data(statistics)
                        .build()
        );
    }
}
