package com.draft.e_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;



@Entity
@Table(name = "customer")

public class Customer extends BaseEntity {
    private String name;
    private String email;

    // Getters and setters
}