package com.demo.saldo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableScheduling
public class SaldoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaldoApplication.class, args);
    }
}
