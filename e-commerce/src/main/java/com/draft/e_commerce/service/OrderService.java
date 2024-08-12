package com.draft.e_commerce.service;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;
import com.draft.e_commerce.repository.ProductRepository;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    public Order placeOrder(Long cartId) {
        try {
            // Cart'ı bul
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new IllegalArgumentException("Cart not found"));


            // CartEntry'leri bul
            Set<CartEntry> cartEntries = cart.getCartEntries();

            // Her bir CartEntry için işlem yap
            for (CartEntry cartEntry : cartEntries) {
                Product product = cartEntry.getProduct();
                int quantity = cartEntry.getQuantity();

                // Ürün stoklarını güncelle
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
            }

            // Order oluştur
            Order order = new Order();
            order.setOrderCode(generateOrderCode());
            order.setCart(cart);
            order.setCustomer(cart.getCustomer());

            long totalPrice = calculateTotalPrice(cartEntries);
            order.setTotalPrice(totalPrice);

            cartRepository.save(cart);

            

            return orderRepository.save(order);
        } catch (IllegalStateException e) {
            logger.error("Error placing order: ", e);
            throw e;
        }
    }

    private long calculateTotalPrice(Set<CartEntry> cartEntries) {
        return cartEntries.stream()
                          .mapToLong(cartEntry -> cartEntry.getProduct().getPrice() * cartEntry.getQuantity())
                          .sum();
    }

    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            logger.warn("Order not found for id: " + id);
        } else {
            logger.info("Order found: " + order);
        }
        return order;
    }
}
