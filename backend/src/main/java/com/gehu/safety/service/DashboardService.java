package com.gehu.safety.service;

import com.gehu.safety.dto.response.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary(int windowDays);
}
