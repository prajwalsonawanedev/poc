package com.poc.orderservice.enums;

public enum OrderStatus {
    CREATED("CREATED"),
    VALIDATED("VALIDATED"),
    REJECTED("REJECTED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    VALIDATION_FAILED("VALIDATIONFAILED"),
    INSUFFICIENT_INVENTORY("INSUFFICIENT INVENTORY"),
    INVENTORY_FAILED("INVENTORY FAILED");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}