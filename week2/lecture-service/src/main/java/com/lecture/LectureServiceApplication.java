package com.lecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.lecture.auth.external.api.feign")
public class LectureServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LectureServiceApplication.class, args);
    }
}
