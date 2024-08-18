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

    @GetMapping("/{id}")
    public CartDTO  getCart(@PathVariable Long id) {
        return cartService.getCart(id);
    }

    @PutMapping("/{id}")
    public CartDTO createCart(@PathVariable Long id, @RequestBody CartDTO cart) {
        cart.setId(id);
        return cartService.createCart(cart,id);
    }

    @DeleteMapping("/{id}/empty")
    public void emptyCart(@PathVariable Long id) {
        cartService.emptyCart(id);
    }

    @PostMapping("/{cartId}/products/{productId}")
    public void addProductToCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.addProductToCart(cartId, productId);
    }

    @DeleteMapping("/{cartId}/products/{productId}")
    public void removeProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);
    }
}
