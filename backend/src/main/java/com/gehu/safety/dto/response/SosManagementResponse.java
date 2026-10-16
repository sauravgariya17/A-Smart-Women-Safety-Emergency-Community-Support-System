package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SosCategory;
import com.gehu.safety.entity.SosStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
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
}
