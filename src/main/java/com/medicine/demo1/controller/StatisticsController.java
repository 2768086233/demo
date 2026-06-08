package com.medicine.demo1.controller;

import com.medicine.demo1.common.Result;
import com.medicine.demo1.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "数据统计", description = "仪表盘数据、效期分布统计")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "首页仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard(@RequestParam Long userId) {
        Map<String, Object> data = statisticsService.getDashboard(userId);
        return Result.success(data);
    }

    @Operation(summary = "效期分布数据")
    @GetMapping("/expiry-distribution")
    public Result<Map<String, Object>> getExpiryDistribution(@RequestParam Long userId) {
        Map<String, Object> data = statisticsService.getExpiryDistribution(userId);
        return Result.success(data);
    }
}
