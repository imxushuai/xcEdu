package com.xuecheng.manage_course.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "course-publish")
public class CoursePublishConfig {
    private String siteId;
    private String templateId;
    private String previewUrl;
    private String pageWebPath;
    private String pagePhysicalPath;
    private String dataUrlPre;
}
