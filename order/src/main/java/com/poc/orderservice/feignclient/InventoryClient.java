package com.poc.orderservice.feignclient;

import com.poc.orderservice.request.InventoryRequestDto;
import com.poc.orderservice.response.ApiResponse;
import com.poc.orderservice.response.InventoryResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "inventory-service", url = "http://localhost:8081/api/inventory")
public interface InventoryClient {

    @PostMapping
    ResponseEntity<ApiResponse<InventoryResponseDto>> createInventory(@RequestBody InventoryRequestDto inventoryRequest);
}