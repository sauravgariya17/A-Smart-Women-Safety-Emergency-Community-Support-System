package com.gehu.safety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustedContactResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String relation;
    private Integer priority;
    private boolean verified;
}
