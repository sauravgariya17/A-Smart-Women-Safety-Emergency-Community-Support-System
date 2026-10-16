package com.gehu.safety.service.impl;

import com.gehu.safety.dto.response.DashboardSummaryResponse;
import com.gehu.safety.dto.response.SafetyAnalyticsResponse;
import com.gehu.safety.entity.IncidentStatus;
import com.gehu.safety.entity.ModerationQueueStatus;
import com.gehu.safety.entity.ModerationStatus;
import com.gehu.safety.entity.ReportSource;
import com.gehu.safety.entity.SosStatus;
import com.gehu.safety.repository.CommunityPostRepository;
import com.gehu.safety.repository.IncidentReportRepository;
import com.gehu.safety.repository.ModerationQueueRepository;
import com.gehu.safety.repository.SosAlertRepository;
import com.gehu.safety.service.DashboardService;
import com.gehu.safety.service.SafetyAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Pure aggregation facade - no new state.
 *
 * INTEGRATION FIX (integration review pass): this class previously
 * re-implemented the exact same incident category/area/trend aggregation
 * AND the k-anonymity suppression threshold that SafetyAnalyticsServiceImpl
 * already provides (duplicate logic, two places to keep in sync, two
 * independently-drifting suppression constants). It now delegates that
 * part to SafetyAnalyticsService and only computes the genuinely new
 * cross-module counts (SOS, community, moderation) here.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final List<IncidentStatus> OPEN_STATUSES = List.of(
            IncidentStatus.SUBMITTED, IncidentStatus.UNDER_REVIEW, IncidentStatus.ASSIGNED, IncidentStatus.IN_PROGRESS);
    private static final List<IncidentStatus> RESOLVED_STATUSES = List.of(
            IncidentStatus.RESOLVED, IncidentStatus.CLOSED);
    private static final List<SosStatus> ACTIVE_SOS_STATUSES = List.of(
            SosStatus.TRIGGERED, SosStatus.ACKNOWLEDGED);

    private final IncidentReportRepository incidentReportRepository;
    private final SosAlertRepository sosAlertRepository;
    private final CommunityPostRepository communityPostRepository;
    private final ModerationQueueRepository moderationQueueRepository;
    private final SafetyAnalyticsService safetyAnalyticsService;

    @Override
    public DashboardSummaryResponse getSummary(int windowDays) {
        // Delegate the incident category/area/trend aggregation + privacy
        // suppression to the single source of truth for that logic.
        SafetyAnalyticsResponse analytics = safetyAnalyticsService.getAnalytics(windowDays);

        long totalIncidents = incidentReportRepository.count();
        long openIncidents = incidentReportRepository.countByStatusIn(OPEN_STATUSES);
        long resolvedIncidents = incidentReportRepository.countByStatusIn(RESOLVED_STATUSES);

        long totalEmergencyEvents = sosAlertRepository.count();
        long activeEmergencyEvents = sosAlertRepository.countByStatusIn(ACTIVE_SOS_STATUSES);

        long totalCommunityPosts = communityPostRepository.count();
        long pendingModerationPosts = communityPostRepository.countByModerationStatus(ModerationStatus.PENDING_REVIEW);

        long modPending = moderationQueueRepository.countByStatus(ModerationQueueStatus.PENDING);
        long modApproved = moderationQueueRepository.countByStatus(ModerationQueueStatus.APPROVED);
        long modRemoved = moderationQueueRepository.countByStatus(ModerationQueueStatus.REMOVED);
        long modAiFlagged = moderationQueueRepository.countByFlaggedBy(ReportSource.AI);
        long modUserFlagged = moderationQueueRepository.countByFlaggedBy(ReportSource.USER);

        return DashboardSummaryResponse.builder()
                .totalIncidents(totalIncidents)
                .openIncidents(openIncidents)
                .resolvedIncidents(resolvedIncidents)
                .incidentsByCategory(analytics.getReportsByCategory())
                .incidentsByArea(analytics.getReportsByArea())
                .incidentTrend(analytics.getTrend())
                .totalEmergencyEvents(totalEmergencyEvents)
                .activeEmergencyEvents(activeEmergencyEvents)
                .totalCommunityPosts(totalCommunityPosts)
                .pendingModerationPosts(pendingModerationPosts)
                .moderationPending(modPending)
                .moderationApproved(modApproved)
                .moderationRemoved(modRemoved)
                .moderationAiFlagged(modAiFlagged)
                .moderationUserFlagged(modUserFlagged)
                .windowDays(windowDays)
                .disclaimer(analytics.getDisclaimer())
                .build();
    }
}
