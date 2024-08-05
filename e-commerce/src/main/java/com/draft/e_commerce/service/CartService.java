package com.draft.e_commerce.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.ProductRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCart(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public void emptyCart(Long id) {
        Optional<Cart> cartOptional = cartRepository.findById(id);
        
        cartOptional.ifPresent(cart -> {
            cart.setProducts(Set.of());
            cartRepository.save(cart);
        });
    }

    public void addProductToCart(Long cartId, Long productId) {

        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isPresent() && productOptional.isPresent()) {
            
            Cart cart = cartOptional.get();
            Set<Product> products = cart.getProducts();

            products.add(productOptional.get());
            cart.setProducts(products);

            cartRepository.save(cart);
        }
    }

    public void removeProductFromCart(Long cartId, Long productId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isPresent() && productOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Set<Product> products = cart.getProducts();
            products.remove(productOptional.get());
            cart.setProducts(products);
            cartRepository.save(cart);
        }
    }
}
