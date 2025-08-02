package com.ecom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Privilege {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
}