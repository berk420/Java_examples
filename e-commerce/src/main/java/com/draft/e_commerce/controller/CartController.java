package com.draft.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.draft.e_commerce.model.DTO.CartDTO;
import com.draft.e_commerce.service.CartService;


@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{cartId}/customer/{customerId}")
    public CartDTO getCart(@PathVariable Long cartId, @PathVariable Long customerId) {
        return cartService.getCart(cartId,customerId);
    }

    @PutMapping("/customer/{customerId}")
    public CartDTO createCart(@PathVariable Long customerId, @RequestBody CartDTO cart) {
        return cartService.createCart(cart,customerId);
    }

    @DeleteMapping("/{cartId}/customer/{customerId}/empty")
    public void emptyCart(@PathVariable Long cartId, @PathVariable Long customerId) {
        cartService.emptyCart(cartId,customerId);
    }

    @PostMapping("/{cartId}/products/{productId}/customer/{customerId}")
    public void addProductToCart(@PathVariable Long cartId, @PathVariable Long customerId, @PathVariable Long productId) {
        cartService.addProductToCart(cartId, productId,customerId);
    }

    @DeleteMapping("/{cartId}/products/{productId}/customer/{customerId}")
    public void removeProductFromCart(@PathVariable Long cartId, @PathVariable Long customerId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId,customerId);
    }
}
