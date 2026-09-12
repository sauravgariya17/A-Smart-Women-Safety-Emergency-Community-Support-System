package com.gehu.safety.service;

import com.gehu.safety.dto.request.JourneyPingRequest;
import com.gehu.safety.dto.request.StartJourneyRequest;
import com.gehu.safety.dto.response.JourneyResponse;
import com.gehu.safety.dto.response.JourneyShareResponse;

import java.util.List;

public interface JourneyService {

    JourneyResponse startJourney(Long userId, StartJourneyRequest request);

    JourneyResponse ping(Long userId, Long journeyId, JourneyPingRequest request);

    JourneyResponse checkIn(Long userId, Long journeyId);

    JourneyResponse cancelJourney(Long userId, Long journeyId);

    JourneyResponse getActiveJourney(Long userId);

    List<JourneyResponse> getHistory(Long userId);

    /** Public, token-based lookup for a trusted contact - no login required. */
    JourneyShareResponse getShareView(String trackingToken);
}
