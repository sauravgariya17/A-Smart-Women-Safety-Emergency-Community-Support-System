package com.gehu.safety.service;

import com.gehu.safety.dto.response.SafetyAnalyticsResponse;

public interface SafetyAnalyticsService {
    SafetyAnalyticsResponse getAnalytics(int windowDays);
}
