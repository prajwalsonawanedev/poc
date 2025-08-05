package com.poc.orderservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ObjectConverter {

    private final ObjectMapper objectMapper;

    public ObjectConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T convertToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Json to Java object");
        }
    }

    public String writeValue(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }

    }
}