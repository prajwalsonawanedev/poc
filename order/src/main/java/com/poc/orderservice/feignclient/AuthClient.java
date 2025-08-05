package com.poc.orderservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "auth-service", url = "http://localhost:8085")
public interface AuthClient {

    @PostMapping("/api/user/validateStringToken")
    ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authorization);
}
