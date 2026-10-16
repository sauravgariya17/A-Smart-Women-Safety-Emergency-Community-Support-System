package com.gehu.safety.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.notification")
@Data
public class NotificationProperties {
    private boolean emailEnabled;
}
