package com.draft.e_commerce.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.ProductRepository;


@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    public Cart getCart(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public void emptyCart(Long id) {
        Optional<Cart> cartOptional = cartRepository.findById(id);

        cartOptional.ifPresent(cart -> {
            // CartEntry'leri temizle
            Set<CartEntry> cartEntries = cart.getCartEntries();
            cartEntries.clear();
            cart.setCartEntries(cartEntries);

            cartRepository.save(cart);
        });

    }
    @Transactional
    public void addProductToCart(Long cartId, Long productId) {
        // Cart ve Product nesnelerini veritabanından al
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Product> productOptional = productRepository.findById(productId);
    
        // Her iki nesne de mevcutsa işleme devam et
        if (cartOptional.isPresent() && productOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Product product = productOptional.get();
    
            // Cart içinde mevcut CartEntry'yi bul
            Optional<CartEntry> cartEntryOptional = cart.getCartEntries().stream()
                .filter(entry -> entry.getProduct().equals(product))
                .findFirst();
    
            CartEntry cartEntry;
    
            if (cartEntryOptional.isPresent()) {
                // Eğer ürün zaten sepette varsa, miktarını artır
                cartEntry = cartEntryOptional.get();
                cartEntry.setQuantity(cartEntry.getQuantity() + 1); // Miktarı artır
            } else {
                // Eğer ürün sepette yoksa, yeni bir CartEntry oluştur
                cartEntry = new CartEntry();
                cartEntry.setCart(cart);
                cartEntry.setProduct(product);
                cartEntry.setQuantity(1); // Yeni CartEntry için varsayılan miktar
            }
    
            try {
                cartEntryRepository.save(cartEntry);
                cart.getCartEntries().add(cartEntry);
    
                // Yeni toplam fiyatı hesapla
                long newTotalPrice = calculateTotalPrice(cart.getCartEntries());
                cart.setTotalPrice((int) newTotalPrice);
    
                cartRepository.save(cart);
            } catch (Exception e) {
                logger.info("ATTENTION");
                logger.info("Error occurred while saving CartEntry or Cart", e);
                throw e; // Exception'u tekrar fırlatmak önemli olabilir
            }
        }
    }
    
    private long calculateTotalPrice(Set<CartEntry> cartEntries) {
        return cartEntries.stream()
                          .mapToLong(cartEntry -> cartEntry.getProduct().getPrice() * cartEntry.getQuantity())
                          .sum();
    }

    public void removeProductFromCart(Long cartId, Long productId) {
        Optional<Cart> cartOptional = cartRepository.findById(cartId);
        Optional<Product> productOptional = productRepository.findById(productId);

        if (cartOptional.isPresent() && productOptional.isPresent()) {
            Cart cart = cartOptional.get();
            Product product = productOptional.get();

            Optional<CartEntry> cartEntryOptional = cart.getCartEntries().stream()
                .filter(entry -> entry.getProduct().equals(product))
                .findFirst();

            if (cartEntryOptional.isPresent()) {
                CartEntry cartEntry = cartEntryOptional.get();

                if (cartEntry.getQuantity() > 1) {
                    cartEntry.setQuantity(cartEntry.getQuantity() - 1); // Miktarı azalt
                    cartEntryRepository.save(cartEntry);
                } else {
                    cart.getCartEntries().remove(cartEntry); // Eğer miktar 1 ise, entry'yi kaldır
                    cartEntryRepository.delete(cartEntry);
                }

                cartRepository.save(cart);
            }
        }
    }
}
