package com.poc.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.poc.orderservice.feignclient")
@EnableTransactionManagement
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.ecom.order",     // your own order service package
        "com.ecom"      // 👈 include this to scan JwtAuthenticationFilter
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
