package com.gehu.safety.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.sos")
@Data
public class SosProperties {
    private int confirmationWindowSeconds;
}
