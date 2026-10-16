package com.gehu.safety.service;

import com.gehu.safety.entity.SosAlert;
import com.gehu.safety.entity.TrustedContact;

import java.util.List;

/**
 * Sends (or, when not configured, honestly SIMULATES and logs) emergency
 * notifications. There is no SMS/police/emergency-service channel here -
 * only email-or-simulation, matching what is actually implemented.
 */
public interface NotificationService {
    void notifyContactsOfSos(SosAlert sosAlert, List<TrustedContact> contacts);
}
