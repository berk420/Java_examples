package com.draft.e_commerce.service.interf;


import com.draft.e_commerce.model.DTO.CartDTO;

public interface CartServiceInterface {

    CartDTO getCart(Long cartId,Long customerId);

    CartDTO createCart(CartDTO cartDTO,Long customerId);

    CartDTO emptyCart(Long cartId,Long customerId);

    CartDTO addProductToCart(Long cartId, Long productId,Long customerId);

    CartDTO removeProductFromCart(Long cartId, Long productId,Long customerId);
}
