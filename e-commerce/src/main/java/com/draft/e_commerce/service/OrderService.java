package com.draft.e_commerce.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.Mapper.DTOMappers;
import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.OrderEntry;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;
import com.draft.e_commerce.service.interf.OrderServiceInterface;

@Service
public class OrderService implements OrderServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    //#region Dependencies

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartEntryService cartEntryService;

    
    @Autowired
    private CartRepository cartRepository;


    //#endregion

    //#region Methods

    @Transactional
    @Override
    public OrderDTO placeOrder(Long cartId) {
        try {
            //validateOrderNotExists(cartId);

            Cart cart = cartService.findById(cartId);

            Order order = createOrder(cart);

            Set<OrderEntry> orderEntries = processCartEntries(cart, order);

            long totalPrice = calculateTotalPrice(orderEntries);

            finalizeOrder(order, orderEntries, totalPrice);

            cleanUpCart(cart, cart.getCartEntries());

            return DTOMappers.mapOrderToDTO(order, cart.getId());

        } catch (IllegalStateException e) {
            logger.error("Error placing order: ", e);
            throw new CustomException(ErrorCode.ORDER_PLACEMENT_FAILED, e);
        }
    }



    @Override
    public OrderDTO getOrderById(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            logger.warn("Order not found for id: " + orderId);
        } else {
            logger.info("Order found: " + order);
        }
        
        Long cartId = cartService.getCartIdByCustomerId(customerId);
        return DTOMappers.mapOrderToDTO(order,cartId);

    }
    //#endregion

    //#region Functions
    public Long getCartIdByCustomerId(Long customerId) {
        return cartRepository.findCartIdByCustomerId(customerId);
    }


    private BigDecimal calculateBasePrice(CartEntry cartEntry) {
        return BigDecimal.valueOf(cartEntry.getProduct().getPrice());
    }
    
    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    


    private Order createOrder(Cart cart) {

        Order order = new Order();
        order.setOrderCode  (generateOrderCode());
        //order.setCart       (cartId);
        order.setCustomer   (cart.getCustomer());
        return order;
    }

    private Set<OrderEntry> processCartEntries(Cart cart, Order order) {
        Set<CartEntry> cartEntries = cart.getCartEntries();
        Set<OrderEntry> orderEntries = new HashSet<>();

        for (CartEntry cartEntry : cartEntries) {
            OrderEntry orderEntry = createOrderEntry(cartEntry, order);
            orderEntries.add(orderEntry);
        }

        return orderEntries;
    }

    private OrderEntry createOrderEntry(CartEntry cartEntry, Order order) {
        OrderEntry orderEntry = new OrderEntry();
        orderEntry.setOrder(order);
        orderEntry.setProduct(cartEntry.getProduct());
        orderEntry.setQuantity(cartEntry.getQuantity());
        orderEntry.setBasePrice(calculateBasePrice(cartEntry));
        return orderEntry;
    }

    private long calculateTotalPrice(Set<OrderEntry> orderEntries) {
        return orderEntries.stream()
                .mapToLong(orderEntry -> orderEntry.getBasePrice().longValue() * orderEntry.getQuantity())
                .sum();
    }

    private void finalizeOrder(Order order, Set<OrderEntry> orderEntries, long totalPrice) {
        order.setTotalPrice(totalPrice);
        order.setOrderEntries(orderEntries);
        //order.setCart(null);
        orderRepository.save(order);
    }

    private void cleanUpCart(Cart cart, Set<CartEntry> cartEntries) {
        cartEntryService.deleteAll(cartEntries);
        cartService.deleteById(cart.getId());
    }
    //#endregion

}