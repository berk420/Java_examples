package com.draft.e_commerce.service.interf;


import com.draft.e_commerce.model.DTO.CartDTO;

public interface CartEntryServiceInterface {

    CartDTO getCart(Long id);

    CartDTO updateCart(CartDTO cartDTO);

    void emptyCart(Long id);

    void addProductToCart(Long cartId, Long productId);

    void removeProductFromCart(Long cartId, Long productId);
}
