package com.draft.e_commerce.model.DTO;

import java.util.Set;

public class CartDTO {

    private Long id;
    private Long customerId;
    private Set<CartEntryDTO> cartEntries;
    private Integer totalPrice;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Set<CartEntryDTO> getCartEntries() {
        return cartEntries;
    }

    public void setCartEntries(Set<CartEntryDTO> cartEntries) {
        this.cartEntries = cartEntries;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
