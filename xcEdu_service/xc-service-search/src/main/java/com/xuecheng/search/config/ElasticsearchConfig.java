package com.xuecheng.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchConfig {

    private String sourceField;
}
