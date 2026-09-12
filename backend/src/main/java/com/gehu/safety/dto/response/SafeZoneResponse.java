package com.gehu.safety.dto.response;

import com.gehu.safety.entity.SafeZoneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SafeZoneResponse {
    private Long id;
    private String name;
    private SafeZoneType type;
    private Double lat;
    private Double lng;
    private String description;
}
