package com.draft.e_commerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_entry")
public class OrderEntry extends BaseEntry {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Getters and Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
