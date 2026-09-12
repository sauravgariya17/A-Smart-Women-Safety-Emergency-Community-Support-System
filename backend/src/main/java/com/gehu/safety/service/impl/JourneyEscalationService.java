package com.gehu.safety.service.impl;

import com.gehu.safety.config.MapScopeProperties;
import com.gehu.safety.entity.Journey;
import com.gehu.safety.entity.JourneyStatus;
import com.gehu.safety.repository.JourneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Runs periodically and escalates any journey that is still ACTIVE well
 * past its expected arrival time - the "auto check the user is okay"
 * behaviour described in the SRS Journey Mode workflow.
 *
 * CURRENT SCOPE: marks the journey ESCALATED and logs it. Full behaviour
 * (creating an SOSAlert record + notifying trusted contacts) is wired in
 * once the SOS and Notification modules exist - see the TODO below. This
 * keeps the integration seam explicit rather than silently absent.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyEscalationService {

    private final JourneyRepository journeyRepository;
    private final MapScopeProperties mapScopeProperties;

    @Scheduled(fixedDelay = 60_000) // every 60 seconds
    @Transactional
    public void escalateOverdueJourneys() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(mapScopeProperties.getJourneyCheckinGraceMinutes());

        List<Journey> overdue = journeyRepository.findByStatusAndExpectedArrivalBefore(JourneyStatus.ACTIVE, cutoff);

        for (Journey journey : overdue) {
            journey.setStatus(JourneyStatus.ESCALATED);
            journeyRepository.save(journey);

            // TODO (SOS module + Notification module, next sessions):
            //   1. Create an SOSAlert record for journey.getUser() at
            //      journey.getLastKnownLat()/Lng()
            //   2. Notify journey.getNotifiedContacts() and campus security
            // Logged for now so this integration point is visible in the demo/viva.
            log.warn("Journey {} for user {} escalated: no check-in {} minutes after expected arrival. " +
                            "Last known location: ({}, {})",
                    journey.getId(), journey.getUser().getId(),
                    mapScopeProperties.getJourneyCheckinGraceMinutes(),
                    journey.getLastKnownLat(), journey.getLastKnownLng());
        }
    }
}
