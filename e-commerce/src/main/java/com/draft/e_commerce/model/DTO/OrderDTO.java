package com.draft.e_commerce.model.DTO;

import java.util.Set;

public class OrderDTO {

    private Long id;
    private String orderCode;
    private Long customerId;
    private Long cartId;
    private long totalPrice;
    private Set<OrderEntryDTO> orderEntries;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Set<OrderEntryDTO> getOrderEntries() {
        return orderEntries;
    }

    public void setOrderEntries(Set<OrderEntryDTO> orderEntries) {
        this.orderEntries = orderEntries;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
               "id=" + id +
               ", orderCode='" + orderCode + '\'' +
               ", customerId=" + customerId +
               ", cartId=" + cartId +
               ", totalPrice=" + totalPrice +
               ", orderEntries=" + orderEntries +
               '}';
    }

    public OrderDTO orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}
