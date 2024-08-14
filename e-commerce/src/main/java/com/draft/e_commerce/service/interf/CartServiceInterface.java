package com.draft.e_commerce.service.interf;

import com.draft.e_commerce.model.Cart;

public interface CartServiceInterface {

    Cart getCart(Long id);

    Cart updateCart(Cart cart);

    void emptyCart(Long id);

    void addProductToCart(Long cartId, Long productId);

    void removeProductFromCart(Long cartId, Long productId);
}
