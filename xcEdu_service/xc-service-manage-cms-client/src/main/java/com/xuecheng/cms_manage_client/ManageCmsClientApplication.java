package com.xuecheng.cms_manage_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = "com.xuecheng.framework.domain.cms")
@ComponentScan(basePackages = "com.xuecheng.framework")
@ComponentScan(basePackages = "com.xuecheng.cms_manage_client")
public class ManageCmsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageCmsClientApplication.class, args);
    }
}
