package com.draft.e_commerce.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartProduct;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartProductRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.ProductRepository;



@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartProductRepository cartProductRepository;

    

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

    @SuppressWarnings("unused")
    public void addProductToCart(Long cartId, Long productId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isPresent() && productOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Product product = productOptional.get();

            Set<Product> products = cart.getProducts(); 
            products.add(product);
            cart.setProducts(products);

            cartRepository.save(cart);

            Optional<CartProduct> cartProductOptional = cartProductRepository.findByIdCartId(cartId);
            CartProduct cartProduct;

            if (cartProductOptional.isPresent()) {
                cartProduct = cartProductOptional.get();
                Integer currentQuantity = cartProduct.getQuantity();
                if (currentQuantity != 1) {
                    currentQuantity += 1; 
                }
                cartProduct.setQuantity(currentQuantity);

            } else {
                cartProduct = new CartProduct();
                cartProduct.setCart(cart);
                cartProduct.setProduct(product);
                cartProduct.setQuantity(1); 
            }

            cartProductRepository.save(cartProduct);

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
