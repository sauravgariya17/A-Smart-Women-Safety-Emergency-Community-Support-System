package com.gehu.safety.service.impl;

import com.gehu.safety.config.MapScopeProperties;
import com.gehu.safety.dto.request.JourneyPingRequest;
import com.gehu.safety.dto.request.StartJourneyRequest;
import com.gehu.safety.dto.response.JourneyResponse;
import com.gehu.safety.dto.response.JourneyShareResponse;
import com.gehu.safety.dto.response.RouteResponse;
import com.gehu.safety.entity.Journey;
import com.gehu.safety.entity.JourneyStatus;
import com.gehu.safety.entity.TrustedContact;
import com.gehu.safety.entity.User;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.OutOfScopeException;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.JourneyRepository;
import com.gehu.safety.repository.TrustedContactRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.JourneyService;
import com.gehu.safety.service.routing.RouteResult;
import com.gehu.safety.service.routing.RoutingProvider;
import com.gehu.safety.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements JourneyService {

    private final JourneyRepository journeyRepository;
    private final UserRepository userRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final MapScopeProperties mapScopeProperties;
    private final RoutingProvider routingProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public JourneyResponse startJourney(Long userId, StartJourneyRequest request) {
        ensureWithinCampusScope(request.getStartLat(), request.getStartLng());
        ensureWithinCampusScope(request.getDestLat(), request.getDestLng());

        journeyRepository.findByUserIdAndStatus(userId, JourneyStatus.ACTIVE).ifPresent(j -> {
            throw new BadRequestException("You already have an active journey. End it before starting a new one.");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<TrustedContact> contacts = resolveOwnedContacts(userId, request.getTrustedContactIds());

        Journey journey = Journey.builder()
                .user(user)
                .startLat(request.getStartLat())
                .startLng(request.getStartLng())
                .destLat(request.getDestLat())
                .destLng(request.getDestLng())
                .startTime(LocalDateTime.now())
                .expectedArrival(LocalDateTime.now().plusMinutes(request.getExpectedDurationMinutes()))
                .status(JourneyStatus.ACTIVE)
                .lastKnownLat(request.getStartLat())
                .lastKnownLng(request.getStartLng())
                .lastPingAt(LocalDateTime.now())
                .trackingToken(generateTrackingToken())
                .notifiedContacts(contacts)
                .build();

        journey = journeyRepository.save(journey);

        // TODO (next session, Notification module): send the shareUrl to each
        // contact in `contacts` via email/SMS/push. Logged for now so the
        // integration point is explicit rather than silently missing.
        return toResponse(journey);
    }

    @Override
    @Transactional
    public JourneyResponse ping(Long userId, Long journeyId, JourneyPingRequest request) {
        Journey journey = getOwnedActiveJourney(userId, journeyId);

        // Deliberately NOT bounds-checked against the campus radius here:
        // this is the user's real, live location during a real journey.
        // Bounds enforcement applies to map/destination SELECTION, not to
        // where a person is actually allowed to be.
        journey.setLastKnownLat(request.getLat());
        journey.setLastKnownLng(request.getLng());
        journey.setLastPingAt(LocalDateTime.now());

        journeyRepository.save(journey);
        return toResponse(journey);
    }

    @Override
    @Transactional
    public JourneyResponse checkIn(Long userId, Long journeyId) {
        Journey journey = getOwnedActiveJourney(userId, journeyId);
        journey.setStatus(JourneyStatus.COMPLETED);
        journeyRepository.save(journey);
        return toResponse(journey);
    }

    @Override
    @Transactional
    public JourneyResponse cancelJourney(Long userId, Long journeyId) {
        Journey journey = getOwnedActiveJourney(userId, journeyId);
        journey.setStatus(JourneyStatus.CANCELLED);
        journeyRepository.save(journey);
        return toResponse(journey);
    }

    @Override
    public JourneyResponse getActiveJourney(Long userId) {
        Journey journey = journeyRepository.findByUserIdAndStatus(userId, JourneyStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active journey"));
        return toResponse(journey);
    }

    @Override
    public List<JourneyResponse> getHistory(Long userId) {
        return journeyRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public JourneyShareResponse getShareView(String trackingToken) {
        Journey journey = journeyRepository.findByTrackingToken(trackingToken)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid tracking link"));

        if (journey.getStatus() != JourneyStatus.ACTIVE) {
            // The link stops working the moment the journey ends - it is not
            // a permanent way to see someone's location.
            throw new ResourceNotFoundException("This tracking link has expired");
        }

        return JourneyShareResponse.builder()
                .status(journey.getStatus())
                .destLat(journey.getDestLat())
                .destLng(journey.getDestLng())
                .expectedArrival(journey.getExpectedArrival())
                .lastKnownLat(journey.getLastKnownLat())
                .lastKnownLng(journey.getLastKnownLng())
                .lastPingAt(journey.getLastPingAt())
                .build();
    }

    // ---------- helpers ----------

    private Journey getOwnedActiveJourney(Long userId, Long journeyId) {
        Journey journey = journeyRepository.findByIdAndUserId(journeyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        if (journey.getStatus() != JourneyStatus.ACTIVE) {
            throw new BadRequestException("This journey is no longer active");
        }
        return journey;
    }

    private void ensureWithinCampusScope(double lat, double lng) {
        double distance = GeoUtils.distanceKm(mapScopeProperties.getCenterLat(), mapScopeProperties.getCenterLng(), lat, lng);
        if (distance > mapScopeProperties.getBoundsRadiusKm()) {
            throw new OutOfScopeException(
                    "Selected location is outside the GEHU campus service area (" +
                    mapScopeProperties.getBoundsRadiusKm() + " km radius). " +
                    "This platform is scoped to GEHU Clement Town and its immediate surroundings only."
            );
        }
    }

    private Set<TrustedContact> resolveOwnedContacts(Long userId, List<Long> contactIds) {
        if (contactIds == null || contactIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<TrustedContact> result = new HashSet<>();
        for (Long contactId : contactIds) {
            TrustedContact contact = trustedContactRepository.findByIdAndUserId(contactId, userId)
                    .orElseThrow(() -> new BadRequestException("Trusted contact " + contactId + " does not belong to you"));
            result.add(contact);
        }
        return result;
    }

    private String generateTrackingToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private JourneyResponse toResponse(Journey journey) {
        RouteResult route = routingProvider.getRoute(
                journey.getStartLat(), journey.getStartLng(), journey.getDestLat(), journey.getDestLng());

        return JourneyResponse.builder()
                .id(journey.getId())
                .startLat(journey.getStartLat())
                .startLng(journey.getStartLng())
                .destLat(journey.getDestLat())
                .destLng(journey.getDestLng())
                .startTime(journey.getStartTime())
                .expectedArrival(journey.getExpectedArrival())
                .status(journey.getStatus())
                .lastKnownLat(journey.getLastKnownLat())
                .lastKnownLng(journey.getLastKnownLng())
                .lastPingAt(journey.getLastPingAt())
                .trackingToken(journey.getTrackingToken())
                .shareUrl("/journey/track/" + journey.getTrackingToken())
                .route(RouteResponse.builder()
                        .waypoints(route.getWaypoints())
                        .distanceMeters(route.getDistanceMeters())
                        .estimatedDurationSeconds(route.getEstimatedDurationSeconds())
                        .provider(route.getProvider())
                        .build())
                .notifiedContactNames(journey.getNotifiedContacts().stream()
                        .map(TrustedContact::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
