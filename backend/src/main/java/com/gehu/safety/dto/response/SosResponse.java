package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SosCategory;
import com.gehu.safety.entity.SosStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor

public class SosResponse {
    private Long id;
    private SosCategory category;
    private Double lat;
    private Double lng;
    private SosStatus status;
    private LocalDateTime confirmationExpiresAt;
    private LocalDateTime triggeredAt;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String notes;

    public SosResponse(Long id, SosCategory category, Double lat, Double lng, SosStatus status, LocalDateTime confirmationExpiresAt, LocalDateTime triggeredAt, LocalDateTime createdAt, LocalDateTime acknowledgedAt, LocalDateTime resolvedAt, String notes) {
        this.id = id;
        this.category = category;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.confirmationExpiresAt = confirmationExpiresAt;
        this.triggeredAt = triggeredAt;
        this.createdAt = createdAt;
        this.acknowledgedAt = acknowledgedAt;
        this.resolvedAt = resolvedAt;
        this.notes = notes;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final SosResponse value = new SosResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder category(SosCategory value) { this.value.category = value; return this; }
        public Builder lat(Double value) { this.value.lat = value; return this; }
        public Builder lng(Double value) { this.value.lng = value; return this; }
        public Builder status(SosStatus value) { this.value.status = value; return this; }
        public Builder confirmationExpiresAt(LocalDateTime value) { this.value.confirmationExpiresAt = value; return this; }
        public Builder triggeredAt(LocalDateTime value) { this.value.triggeredAt = value; return this; }
        public Builder createdAt(LocalDateTime value) { this.value.createdAt = value; return this; }
        public Builder acknowledgedAt(LocalDateTime value) { this.value.acknowledgedAt = value; return this; }
        public Builder resolvedAt(LocalDateTime value) { this.value.resolvedAt = value; return this; }
        public Builder notes(String value) { this.value.notes = value; return this; }
        public SosResponse build() { return value; }
    }
}
