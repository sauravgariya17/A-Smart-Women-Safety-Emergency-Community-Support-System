package com.gehu.safety.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class TrustedContactResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String relation;
    private Integer priority;
    private boolean verified;

    public TrustedContactResponse(Long id, String name, String phone, String email, String relation, Integer priority, boolean verified) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.relation = relation;
        this.priority = priority;
        this.verified = verified;
    }
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final TrustedContactResponse value = new TrustedContactResponse();
        public Builder id(Long value) { this.value.id = value; return this; }
        public Builder name(String value) { this.value.name = value; return this; }
        public Builder phone(String value) { this.value.phone = value; return this; }
        public Builder email(String value) { this.value.email = value; return this; }
        public Builder relation(String value) { this.value.relation = value; return this; }
        public Builder priority(Integer value) { this.value.priority = value; return this; }
        public Builder verified(boolean value) { this.value.verified = value; return this; }
        public TrustedContactResponse build() { return value; }
    }
}
