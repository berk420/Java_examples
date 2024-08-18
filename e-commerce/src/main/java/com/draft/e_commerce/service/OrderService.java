package com.draft.e_commerce.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.DTO.OrderEntryDTO;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.OrderEntry;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;
import com.draft.e_commerce.repository.ProductRepository;
import com.draft.e_commerce.service.interf.OrderServiceInterface;

@Service
public class OrderService implements OrderServiceInterface {

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
    @Override
    public OrderDTO placeOrder(Long cartId) {
        try {
            boolean orderExists = orderRepository.existsByCart_Id(cartId);
            if (orderExists) {
                throw new CustomException(ErrorCode.ORDER_ALREADY_EXISTS,null);
            }

            Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND,null));

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
                orderEntry.setProduct(cartEntry.getProduct()); // Product ilişkisinin doğru set edilmesi
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
            order = orderRepository.save(order);
    
            // CartEntry'leri silmeden önce Cart ID'yi kaydedin
            Long savedCartId = cart.getId();
    
            // Order'ın Cart referansını null yap
            order.setCart(null);
            orderRepository.save(order); // Order'ı tekrar kaydedin
    
            // CartEntry'leri sil

            cartEntryRepository.deleteAll(cartEntries);
    
            // Cart'ı sil
            cartRepository.deleteById(savedCartId);

            return mapOrderToDTO(order,savedCartId);
    
    
        } catch (IllegalStateException e) {
        logger.error("Error placing order: ", e);
        throw new CustomException(ErrorCode.ORDER_PLACEMENT_FAILED, e);
    }   
        }
    
    

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            logger.warn("Order not found for id: " + id);
        } else {
            logger.info("Order found: " + order);
        }
        //return mapOrderToDTO(order);
        return mapOrderToDTO(order,order.getCart().getId());

    }

    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    private OrderDTO mapOrderToDTO(Order order,long savedCartId) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setCustomerId(order.getCustomer().getId());
        orderDTO.setCartId(savedCartId);
        orderDTO.setTotalPrice(order.getTotalPrice());

        // Düzeltilmiş DTO mapping
        Set<OrderEntryDTO> orderEntryDTOs = order.getOrderEntries().stream()
                .map(entry -> {
                    OrderEntryDTO dto = new OrderEntryDTO();
                    dto.setId(entry.getId());
                    dto.setOrderId(entry.getOrder().getId());
                    dto.setProductId(entry.getProduct().getId()); // Product ID'yi doğru bir şekilde alıyoruz
                    dto.setQuantity(entry.getQuantity());
                    dto.setBasePrice(entry.getBasePrice());
                    return dto;
                })
                .collect(Collectors.toSet());

        orderDTO.setOrderEntries(orderEntryDTOs);

        return orderDTO;
    }
}
