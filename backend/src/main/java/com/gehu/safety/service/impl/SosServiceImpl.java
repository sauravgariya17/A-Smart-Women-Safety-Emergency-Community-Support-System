package com.gehu.safety.service.impl;

import com.gehu.safety.config.SosProperties;
import com.gehu.safety.dto.request.ResolveSosRequest;
import com.gehu.safety.dto.request.TriggerSosRequest;
import com.gehu.safety.dto.response.SosManagementResponse;
import com.gehu.safety.dto.response.SosResponse;
import com.gehu.safety.entity.*;
import com.gehu.safety.exception.BadRequestException;
import com.gehu.safety.exception.ResourceNotFoundException;
import com.gehu.safety.repository.SosAlertRepository;
import com.gehu.safety.repository.TrustedContactRepository;
import com.gehu.safety.repository.UserRepository;
import com.gehu.safety.service.AuditService;
import com.gehu.safety.service.NotificationService;
import com.gehu.safety.service.SosService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SosServiceImpl implements SosService {

    private static final List<SosStatus> NON_TERMINAL = List.of(
            SosStatus.PENDING_CONFIRMATION, SosStatus.TRIGGERED, SosStatus.ACKNOWLEDGED);
    private static final List<SosStatus> DASHBOARD_STATUSES = List.of(
            SosStatus.TRIGGERED, SosStatus.ACKNOWLEDGED);

    private final SosAlertRepository sosAlertRepository;
    private final UserRepository userRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final SosProperties sosProperties;
    private final NotificationService notificationService;
    private final AuditService auditService;


    public SosServiceImpl(SosAlertRepository sosAlertRepository,
            UserRepository userRepository,
            TrustedContactRepository trustedContactRepository,
            SosProperties sosProperties,
            NotificationService notificationService,
            AuditService auditService) {
        this.sosAlertRepository = sosAlertRepository;
        this.userRepository = userRepository;
        this.trustedContactRepository = trustedContactRepository;
        this.sosProperties = sosProperties;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }
    @Override
    @Transactional
    public SosResponse trigger(Long userId, TriggerSosRequest request) {
        sosAlertRepository.findFirstByUserIdAndStatusIn(userId, NON_TERMINAL).ifPresent(a -> {
            throw new BadRequestException("You already have an active SOS in progress");
        });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SosAlert alert = SosAlert.builder()
                .user(user)
                .category(request.getCategory())
                .lat(request.getLat())
                .lng(request.getLng())
                .status(SosStatus.PENDING_CONFIRMATION)
                .confirmationExpiresAt(LocalDateTime.now().plusSeconds(sosProperties.getConfirmationWindowSeconds()))
                .build();

        alert = sosAlertRepository.save(alert);

        auditService.record(userId, "SOS_TRIGGER_INITIATED", "SOS_ALERT", alert.getId(),
                "Awaiting confirmation within " + sosProperties.getConfirmationWindowSeconds() + "s");

        return toResponse(alert);
    }

    @Override
    @Transactional
    public SosResponse confirm(Long userId, Long sosId) {
        SosAlert alert = sosAlertRepository.findByIdAndUserId(sosId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS event not found"));

        if (alert.getStatus() != SosStatus.PENDING_CONFIRMATION) {
            throw new BadRequestException("This SOS cannot be confirmed in its current state (" + alert.getStatus() + ")");
        }
        if (alert.getConfirmationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Confirmation window expired. Please trigger SOS again.");
        }

        alert.setStatus(SosStatus.TRIGGERED);
        alert.setTriggeredAt(LocalDateTime.now());
        sosAlertRepository.save(alert);

        // Notifications fire ONLY here - the confirmation is what makes them real.
        List<TrustedContact> contacts = trustedContactRepository.findByUserIdOrderByPriorityAsc(userId);
        notificationService.notifyContactsOfSos(alert, contacts);

        auditService.record(userId, "SOS_CONFIRMED_AND_TRIGGERED", "SOS_ALERT", alert.getId(),
                "Notified " + contacts.size() + " trusted contact(s)");

        return toResponse(alert);
    }

    @Override
    @Transactional
    public SosResponse cancel(Long userId, Long sosId) {
        SosAlert alert = sosAlertRepository.findByIdAndUserId(sosId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS event not found"));

        if (!NON_TERMINAL.contains(alert.getStatus())) {
            throw new BadRequestException("This SOS is already closed");
        }

        if (alert.getStatus() == SosStatus.PENDING_CONFIRMATION) {
            alert.setStatus(SosStatus.CANCELLED);
        } else {
            // Was already TRIGGERED/ACKNOWLEDGED (contacts may have been notified) -
            // record it as a false alarm rather than a silent cancellation.
            alert.setStatus(SosStatus.FALSE_ALARM);
            alert.setResolvedAt(LocalDateTime.now());
            alert.setNotes(appendNote(alert.getNotes(), "Cancelled by user - marked safe"));
        }
        sosAlertRepository.save(alert);

        auditService.record(userId, "SOS_CANCELLED_BY_USER", "SOS_ALERT", alert.getId(), null);

        return toResponse(alert);
    }

    @Override
    public SosResponse getMyActive(Long userId) {
        SosAlert alert = sosAlertRepository.findFirstByUserIdAndStatusIn(userId, NON_TERMINAL)
                .orElseThrow(() -> new ResourceNotFoundException("No active SOS"));
        return toResponse(alert);
    }

    @Override
    public List<SosResponse> getMyHistory(Long userId) {
        return sosAlertRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SosManagementResponse> getActiveQueue() {
        return sosAlertRepository.findByStatusInOrderByCreatedAtAsc(DASHBOARD_STATUSES).stream()
                .map(this::toManagementResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SosManagementResponse acknowledge(Long staffUserId, Long sosId) {
        SosAlert alert = sosAlertRepository.findById(sosId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS event not found"));

        if (alert.getStatus() != SosStatus.TRIGGERED) {
            throw new BadRequestException("Only a TRIGGERED alert can be acknowledged (current: " + alert.getStatus() + ")");
        }

        User staff = userRepository.findById(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        alert.setStatus(SosStatus.ACKNOWLEDGED);
        alert.setAcknowledgedBy(staff);
        alert.setAcknowledgedAt(LocalDateTime.now());
        sosAlertRepository.save(alert);

        auditService.record(staffUserId, "SOS_ACKNOWLEDGED", "SOS_ALERT", alert.getId(), null);

        return toManagementResponse(alert);
    }

    @Override
    @Transactional
    public SosManagementResponse resolve(Long staffUserId, Long sosId, ResolveSosRequest request) {
        SosAlert alert = sosAlertRepository.findById(sosId)
                .orElseThrow(() -> new ResourceNotFoundException("SOS event not found"));

        if (alert.getStatus() != SosStatus.ACKNOWLEDGED) {
            throw new BadRequestException("Only an acknowledged alert can be resolved (current: " + alert.getStatus() + ")");
        }
        if (request.getOutcome() != SosStatus.RESOLVED && request.getOutcome() != SosStatus.FALSE_ALARM) {
            throw new BadRequestException("Outcome must be RESOLVED or FALSE_ALARM");
        }

        alert.setStatus(request.getOutcome());
        alert.setResolvedAt(LocalDateTime.now());
        alert.setNotes(appendNote(alert.getNotes(), request.getNotes()));
        sosAlertRepository.save(alert);

        auditService.record(staffUserId, "SOS_" + request.getOutcome(), "SOS_ALERT", alert.getId(), request.getNotes());

        return toManagementResponse(alert);
    }

    // ---------- mapping helpers ----------

    private String appendNote(String existing, String addition) {
        if (addition == null || addition.isBlank()) return existing;
        return (existing == null || existing.isBlank()) ? addition : existing + " | " + addition;
    }

    private SosResponse toResponse(SosAlert alert) {
        return SosResponse.builder()
                .id(alert.getId())
                .category(alert.getCategory())
                .lat(alert.getLat())
                .lng(alert.getLng())
                .status(alert.getStatus())
                .confirmationExpiresAt(alert.getConfirmationExpiresAt())
                .triggeredAt(alert.getTriggeredAt())
                .createdAt(alert.getCreatedAt())
                .acknowledgedAt(alert.getAcknowledgedAt())
                .resolvedAt(alert.getResolvedAt())
                .notes(alert.getNotes())
                .build();
    }

    private SosManagementResponse toManagementResponse(SosAlert alert) {
        return SosManagementResponse.builder()
                .id(alert.getId())
                .requesterUserId(alert.getUser().getId())
                .requesterName(alert.getUser().getName())
                .requesterPhone(alert.getUser().getPhoneEncrypted()) // AES decryption is planned after Stage 1 adds encrypted phone storage.
                .category(alert.getCategory())
                .lat(alert.getLat())
                .lng(alert.getLng())
                .status(alert.getStatus())
                .triggeredAt(alert.getTriggeredAt())
                .acknowledgedByName(alert.getAcknowledgedBy() != null ? alert.getAcknowledgedBy().getName() : null)
                .acknowledgedAt(alert.getAcknowledgedAt())
                .resolvedAt(alert.getResolvedAt())
                .notes(alert.getNotes())
                .build();
    }
}
