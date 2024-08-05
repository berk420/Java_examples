package com.draft.e_commerce.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    public Order placeOrder(Long cartId) {
        try {
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
            if (cart.isOrderState()) {
                throw new IllegalStateException("This cart is already associated with an order.");
            }

            Order order = new Order();
            order.setOrderCode(generateOrderCode());
            order.setCart(cart);
            order.setCustomer(cart.getCustomer());
            long totalPrice = calculateTotalPrice(cart);
            order.setTotalPrice(totalPrice);

            cart.setOrderState(true);
            cartRepository.save(cart);

            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error placing order: ", e);
            throw e;
        }
    }

    private long calculateTotalPrice(Cart cart) {
        return cart.getProducts().stream().mapToLong(product -> product.getPrice()).sum();
    }

    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}
