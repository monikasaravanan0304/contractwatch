package com.contractwatch.contractwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ContractwatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractwatchApplication.class, args);
    }

}