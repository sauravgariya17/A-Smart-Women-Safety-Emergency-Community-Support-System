package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SosCategory;
import com.gehu.safety.entity.SosStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * CONTRAST WITH THE COMMUNITY MODULE'S DTOs ON PURPOSE: those hide identity
 * by design because the community is anonymous. An emergency is the
 * opposite case - campus security responding to a real SOS legitimately
 * needs to know who is in danger and how to reach them, so this DTO
 * includes name/phone. Access to this DTO is restricted to
 * SECURITY_STAFF/ADMIN only (SecurityConfig + @PreAuthorize), and every
 * view/action against it should be assumed auditable via AuditLog.
 */
@Data
@NoArgsConstructor

public class SosManagementResponse {
    private Long id;
    private Long requesterUserId;
    private String requesterName;
    private String requesterPhone;
    private SosCategory category;
    private Double lat;
    private Double lng;
    private SosStatus status;
    private LocalDateTime triggeredAt;
    private String acknowledgedByName;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String notes;

    public SosManagementResponse(Long id, Long requesterUserId, String requesterName, String requesterPhone, SosCategory category, Double lat, Double lng, SosStatus status, LocalDateTime triggeredAt, String acknowledgedByName, LocalDateTime acknowledgedAt, LocalDateTime resolvedAt, String notes) {
        this.id = id;
        this.requesterUserId = requesterUserId;
        this.requesterName = requesterName;
        this.requesterPhone = requesterPhone;
        this.category = category;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.acknowledgedByName = acknowledgedByName;
        this.acknowledgedAt = acknowledgedAt;
        this.resolvedAt = resolvedAt;
        this.notes = notes;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SosManagementResponse value = new SosManagementResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder requesterUserId(Long value) { this.value.requesterUserId = value; return this; }
        public Builder requesterName(String value) { this.value.requesterName = value; return this; }
        public Builder requesterPhone(String value) { this.value.requesterPhone = value; return this; }
        public Builder category(SosCategory value) { this.value.category = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder status(SosStatus value) { this.value.status = value; return this; }
        public Builder triggeredAt(LocalDateTime value) { this.value.triggeredAt = value; return this; }
        public Builder acknowledgedByName(String value) { this.value.acknowledgedByName = value; return this; }
        public Builder acknowledgedAt(LocalDateTime value) { this.value.acknowledgedAt = value; return this; }
        public Builder resolvedAt(LocalDateTime value) { this.value.resolvedAt = value; return this; }
        public Builder notes(String value) { this.value.notes = value; return this; }
        public SosManagementResponse build() { return value; }
    }
}
