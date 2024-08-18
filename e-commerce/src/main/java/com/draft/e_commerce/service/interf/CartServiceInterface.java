package com.draft.e_commerce.service.interf;


import com.draft.e_commerce.model.DTO.CartDTO;

public interface CartServiceInterface {

    CartDTO getCart(Long id);

    CartDTO createCart(CartDTO cartDTO,Long id);

    void emptyCart(Long id);

    void addProductToCart(Long cartId, Long productId);

    void removeProductFromCart(Long cartId, Long productId);
}
