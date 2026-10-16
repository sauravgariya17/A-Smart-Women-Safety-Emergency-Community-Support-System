package com.gehu.safety.dto.response;

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
public class SafetyAnalyticsResponse {
    private Map<String, Long> reportsByCategory;
    private Map<String, Long> reportsByArea;
    private List<DailyCount> trend;
    private int windowDays;
    private String disclaimer;

    public SafetyAnalyticsResponse(Map<String, Long> reportsByCategory,
                                   Map<String, Long> reportsByArea,
                                   List<DailyCount> trend,
                                   int windowDays,
                                   String disclaimer) {
        this.reportsByCategory = reportsByCategory;
        this.reportsByArea = reportsByArea;
        this.trend = trend;
        this.windowDays = windowDays;
        this.disclaimer = disclaimer;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final SafetyAnalyticsResponse value = new SafetyAnalyticsResponse();
        public Builder reportsByCategory(Map<String, Long> value) { this.value.reportsByCategory = value; return this; }
        public Builder reportsByArea(Map<String, Long> value) { this.value.reportsByArea = value; return this; }
        public Builder trend(List<DailyCount> value) { this.value.trend = value; return this; }
        public Builder windowDays(int value) { this.value.windowDays = value; return this; }
        public Builder disclaimer(String value) { this.value.disclaimer = value; return this; }
        public SafetyAnalyticsResponse build() { return value; }
    }

    @Data
    @NoArgsConstructor
    public static class DailyCount {
        private String date;
        private long count;

        public DailyCount(String date, long count) {
            this.date = date;
            this.count = count;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private final DailyCount value = new DailyCount();
            public Builder date(String value) { this.value.date = value; return this; }
            public Builder count(long value) { this.value.count = value; return this; }
            public DailyCount build() { return value; }
        }
    }
}
