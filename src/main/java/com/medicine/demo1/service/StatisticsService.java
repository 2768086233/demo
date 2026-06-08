package com.medicine.demo1.service;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getDashboard(Long userId);

    Map<String, Object> getExpiryDistribution(Long userId);
}
