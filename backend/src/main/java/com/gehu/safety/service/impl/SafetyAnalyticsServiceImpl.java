package com.gehu.safety.service.impl;

import com.gehu.safety.dto.response.SafetyAnalyticsResponse;
import com.gehu.safety.repository.IncidentReportRepository;
import com.gehu.safety.service.SafetyAnalyticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AGGREGATED ANALYTICS ONLY. Two privacy rules enforced here, not just in
 * documentation:
 *  1. No individual incident is ever returned - only GROUP BY counts.
 *  2. k-ANONYMITY SUPPRESSION: any area-group with fewer than
 *     MIN_GROUP_SIZE reports is dropped entirely before the response is
 *     built. On a small campus, "1 incident near X" is close to naming a
 *     specific report/person; only categories (never tied to a single
 *     small place) are exempt from this suppression.
 *
 * This module does NOT claim any area is "objectively dangerous" - it
 * reflects what was reported, not verified ground truth, and undercounts
 * anything nobody chose to report. The disclaimer field carries this
 * caveat into every API response so the frontend cannot drop it by accident.
 */
@Service
public class SafetyAnalyticsServiceImpl implements SafetyAnalyticsService {

    private static final int MIN_GROUP_SIZE = 3; // k-anonymity threshold for area suppression

    private final IncidentReportRepository incidentReportRepository;


    public SafetyAnalyticsServiceImpl(IncidentReportRepository incidentReportRepository) {
        this.incidentReportRepository = incidentReportRepository;
    }
    @Override
    public SafetyAnalyticsResponse getAnalytics(int windowDays) {
        LocalDateTime since = LocalDateTime.now().minusDays(windowDays);

        Map<String, Long> byCategory = incidentReportRepository.countByCategorySince(since).stream()
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> (Long) row[1],
                        (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> byArea = incidentReportRepository.countByAreaSince(since).stream()
                .filter(row -> ((Long) row[1]) >= MIN_GROUP_SIZE) // suppression rule
                .collect(Collectors.toMap(
                        row -> String.valueOf(row[0]),
                        row -> (Long) row[1],
                        (a, b) -> a, LinkedHashMap::new));

        List<SafetyAnalyticsResponse.DailyCount> trend = incidentReportRepository.countByDaySince(since).stream()
                .map(row -> SafetyAnalyticsResponse.DailyCount.builder()
                        .date(String.valueOf(row[0]))
                        .count((Long) row[1])
                        .build())
                .collect(Collectors.toList());

        return SafetyAnalyticsResponse.builder()
                .reportsByCategory(byCategory)
                .reportsByArea(byArea)
                .trend(trend)
                .windowDays(windowDays)
                .disclaimer("These figures reflect reported incidents only, not verified or objective danger " +
                        "levels. Areas with fewer than " + MIN_GROUP_SIZE + " reports are omitted to protect " +
                        "reporter privacy on a small campus.")
                .build();
    }
}
