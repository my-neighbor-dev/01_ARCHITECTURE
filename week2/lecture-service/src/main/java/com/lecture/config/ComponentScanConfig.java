package com.lecture.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.lecture.user",
    "com.lecture.auth"
})
public class ComponentScanConfig {
}
