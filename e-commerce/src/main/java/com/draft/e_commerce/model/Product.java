package com.draft.e_commerce.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table(name = "product")

public class Product extends BaseEntity {
    private String name;
    private String description;
    private long price;
    public void setId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setId'");
    }

    // Getters and setters
}