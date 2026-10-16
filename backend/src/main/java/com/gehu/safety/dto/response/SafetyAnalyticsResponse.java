package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * AGGREGATED DATA ONLY - see SafetyAnalyticsServiceImpl for the k-anonymity
 * suppression rule (groups below the threshold are omitted entirely, never
 * shown as "1 incident here"). This response never proves an area is
 * "objectively dangerous" - it reflects what was reported, not verified
 * ground truth, and the frontend/report text must say so explicitly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafetyAnalyticsResponse {
    private Map<String, Long> reportsByCategory;
    private Map<String, Long> reportsByArea;      // areas with fewer than the suppression threshold are omitted
    private List<DailyCount> trend;                // reports per day over the window
    private int windowDays;
    private String disclaimer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyCount {
        private String date;
        private long count;
    }
}
