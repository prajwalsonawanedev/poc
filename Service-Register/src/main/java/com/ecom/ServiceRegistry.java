package com.ecom;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistry {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}