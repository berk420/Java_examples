package com.draft.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.CartProduct;
import com.draft.e_commerce.model.CartProductId;
import com.draft.e_commerce.repository.CartProductRepository;

@Service
public class CartProductService {

    @Autowired
    private CartProductRepository cartProductRepository;

    public CartProduct getCartProductById(Long cartId, Long productId) {
        CartProductId cartProductId = new CartProductId();
        cartProductId.setCartId(cartId);
        cartProductId.setProductId(productId);

        return cartProductRepository.findById(cartProductId)
            .orElseThrow(() -> new IllegalArgumentException("CartProduct not found"));
    }
}
