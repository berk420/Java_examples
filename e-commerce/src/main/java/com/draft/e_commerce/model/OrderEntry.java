package com.draft.e_commerce.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_entry")
public class OrderEntry extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "base_price", nullable = false)
    private BigDecimal base_price;

    /* 
    public OrderEntry(Order order, int quantity, BigDecimal base_price) {
        this.order = order;
        this.base_price = base_price;
        this.quantity = quantity;
    }
    */

    public Order getOrder() {
        return order;
    }
    

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBasePrice() {
        return base_price;
    }

    public void setBasePrice(BigDecimal base_price) {
        this.base_price = base_price;
    }


}
