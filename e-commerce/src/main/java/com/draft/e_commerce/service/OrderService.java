package com.draft.e_commerce.service;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.OrderEntry;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;
import com.draft.e_commerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public Order placeOrder(Long cartId) {
        try {
            boolean orderExists = orderRepository.existsByCart_Id(cartId);
            if (orderExists) {
                throw new IllegalStateException("Order with this cart already exists");
            }
            Cart cart = cartRepository.findById(cartId)
                                    .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

            Order order = new Order();
            order.setOrderCode(generateOrderCode());
            order.setCart(cart);
            order.setCustomer(cart.getCustomer());

            Set<CartEntry> cartEntries = cart.getCartEntries();
            Set<OrderEntry> orderEntries = new HashSet<>();

            long totalPrice = 0;

            for (CartEntry cartEntry : cartEntries) {
                // Create a new OrderEntry for each CartEntry
                OrderEntry orderEntry = new OrderEntry();
                orderEntry.setOrder(order);
                orderEntry.setQuantity(cartEntry.getQuantity());

                // Set the base price as the current product price
                BigDecimal basePrice = BigDecimal.valueOf(cartEntry.getProduct().getPrice());
                orderEntry.setBasePrice(basePrice);

                // Calculate the total price
                totalPrice += basePrice.longValue() * cartEntry.getQuantity();

                // Add OrderEntry to the set
                orderEntries.add(orderEntry);
            }

            // Set the total price for the order
            order.setTotalPrice(totalPrice);
            order.setOrderEntries(orderEntries);

            // Save the order and cascade the order entries
            return orderRepository.save(order);

        } catch (IllegalStateException e) {
            logger.error("Error placing order: ", e);
            throw e;
        }
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
