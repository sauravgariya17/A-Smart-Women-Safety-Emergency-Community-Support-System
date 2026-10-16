package com.gehu.safety.service.impl;

import com.gehu.safety.entity.SosAlert;
import com.gehu.safety.entity.SosStatus;
import com.gehu.safety.repository.SosAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Prevents a dropped confirm() network call from leaving a phantom
 * "in-progress" emergency forever. No notifications were ever sent for
 * these (PENDING_CONFIRMATION never fires them), so auto-cancelling here
 * is silent and safe by design.
 */
@Service
public class SosConfirmationExpiryService {

    private static final Logger log = LoggerFactory.getLogger(SosConfirmationExpiryService.class);

    private final SosAlertRepository sosAlertRepository;


    public SosConfirmationExpiryService(SosAlertRepository sosAlertRepository) {
        this.sosAlertRepository = sosAlertRepository;
    }
    @Scheduled(fixedDelay = 10_000) // every 10 seconds - the confirmation window itself is short
    @Transactional
    public void expireUnconfirmedAlerts() {
        List<SosAlert> expired = sosAlertRepository.findByStatusAndConfirmationExpiresAtBefore(
                SosStatus.PENDING_CONFIRMATION, LocalDateTime.now());

        for (SosAlert alert : expired) {
            alert.setStatus(SosStatus.CANCELLED);
            sosAlertRepository.save(alert);
            log.info("SOS {} auto-cancelled: confirmation window expired without user action", alert.getId());
        }
    }
}
