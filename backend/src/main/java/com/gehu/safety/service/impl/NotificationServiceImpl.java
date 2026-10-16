package com.gehu.safety.service.impl;

import com.gehu.safety.config.NotificationProperties;
import com.gehu.safety.entity.*;
import com.gehu.safety.repository.NotificationLogRepository;
import com.gehu.safety.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ARCHITECTURE NOTE (read before extending this class):
 * - If app.notification.email-enabled=true AND SMTP is reachable: sends a
 *   real email via JavaMailSender, logs NotificationLog(channel=EMAIL, status=SENT).
 * - If email is disabled, OR the send throws (bad credentials, no network,
 *   SMTP unreachable): falls back to a logged simulation
 *   (channel=CONSOLE_SIMULATION, status=SIMULATED) and continues to the next
 *   contact rather than aborting the whole notification round - this is the
 *   "graceful network/API failure handling" requirement in concrete form.
 * - No SMS, no push, no police/emergency-authority channel exists here.
 *   Do not claim otherwise in the viva/report.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationProperties notificationProperties;
    private final NotificationLogRepository notificationLogRepository;
    private final JavaMailSender mailSender; // auto-configured by spring-boot-starter-mail

    public NotificationServiceImpl(NotificationProperties notificationProperties,
            NotificationLogRepository notificationLogRepository,
            JavaMailSender mailSender) {
        this.notificationProperties = notificationProperties;
        this.notificationLogRepository = notificationLogRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void notifyContactsOfSos(SosAlert sosAlert, List<TrustedContact> contacts) {
        String subject = "[GEHU Safety Alert] Emergency notification";
        String body = buildMessage(sosAlert);

        for (TrustedContact contact : contacts) {
            String recipient = contact.getEmail();
            if (recipient == null || recipient.isBlank()) {
                // No email on file for this contact - log it so the gap is visible, not silent.
                saveLog(sosAlert, contact, NotificationChannel.CONSOLE_SIMULATION,
                        "(no email on file for " + contact.getName() + ")", NotificationLogStatus.SIMULATED);
                log.warn("SOS {}: trusted contact {} has no email on file, notification simulated only",
                        sosAlert.getId(), contact.getName());
                continue;
            }

            if (notificationProperties.isEmailEnabled()) {
                try {
                    sendEmail(recipient, subject, body);
                    saveLog(sosAlert, contact, NotificationChannel.EMAIL, recipient, NotificationLogStatus.SENT);
                    continue;
                } catch (Exception ex) {
                    // Real send failed (bad SMTP config, no network, etc.) - fall through to simulation.
                    log.error("SOS {}: real email to {} failed ({}), falling back to simulation",
                            sosAlert.getId(), recipient, ex.getMessage());
                }
            }

            // Simulation path - either email is disabled, or the real send just failed above.
            log.info("[SIMULATED NOTIFICATION] To: {} | Subject: {} | Body: {}", recipient, subject, body);
            saveLog(sosAlert, contact, NotificationChannel.CONSOLE_SIMULATION, recipient, NotificationLogStatus.SIMULATED);
        }
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    private String buildMessage(SosAlert sosAlert) {
        return String.format(
                "%s has triggered a safety alert on the GEHU Smart Safety platform.%n" +
                "Category: %s%n" +
                "Location (approx.): %.6f, %.6f%n" +
                "Time: %s%n%n" +
                "This is an automated notification. Please try to reach them directly.",
                sosAlert.getUser().getName(),
                sosAlert.getCategory(),
                sosAlert.getLat(), sosAlert.getLng(),
                sosAlert.getTriggeredAt()
        );
    }

    private void saveLog(SosAlert sosAlert, TrustedContact contact, NotificationChannel channel,
                          String recipient, NotificationLogStatus status) {
        NotificationLog logEntry = NotificationLog.builder()
                .sosAlert(sosAlert)
                .contact(contact)
                .channel(channel)
                .recipient(recipient)
                .status(status)
                .build();
        notificationLogRepository.save(logEntry);
    }
}
