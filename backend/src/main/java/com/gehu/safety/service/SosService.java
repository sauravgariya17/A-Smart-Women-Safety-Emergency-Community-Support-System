package com.gehu.safety.service;

import com.gehu.safety.dto.request.ResolveSosRequest;
import com.gehu.safety.dto.request.TriggerSosRequest;
import com.gehu.safety.dto.response.SosManagementResponse;
import com.gehu.safety.dto.response.SosResponse;

import java.util.List;

public interface SosService {

    /** Creates a PENDING_CONFIRMATION event. No one is notified yet. */
    SosResponse trigger(Long userId, TriggerSosRequest request);

    /** Moves PENDING_CONFIRMATION -> TRIGGERED and fires notifications. */
    SosResponse confirm(Long userId, Long sosId);

    /** User-initiated stand-down at any pre-RESOLVED stage. */
    SosResponse cancel(Long userId, Long sosId);

    SosResponse getMyActive(Long userId);

    List<SosResponse> getMyHistory(Long userId);

    // ---- Security/Admin dashboard ----

    List<SosManagementResponse> getActiveQueue();

    SosManagementResponse acknowledge(Long staffUserId, Long sosId);

    SosManagementResponse resolve(Long staffUserId, Long sosId, ResolveSosRequest request);
}
