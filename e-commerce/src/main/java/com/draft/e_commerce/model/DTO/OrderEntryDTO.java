package com.draft.e_commerce.model.DTO;

import java.math.BigDecimal;

public class OrderEntryDTO {

    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal basePrice;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return "OrderEntryDTO{" +
               "id=" + id +
               ", orderId=" + orderId +
               ", productId=" + productId +
               ", quantity=" + quantity +
               ", basePrice=" + basePrice +
               '}';
    }
}
