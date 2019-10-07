package com.xuecheng.manage_media.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "xc-service-manage-media")
public class MediaServiceProperties {
    private String uploadLocation;
}
