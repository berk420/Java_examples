package com.draft.e_commerce.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.Mapper.DTOMappersOrder;
import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.OrderEntry;
import com.draft.e_commerce.model.Product;
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
    private ProductService productService; // ProductService ekleniyor
    
    @Autowired
    private DTOMappersOrder dTOMappersOrder;

    //#endregion

    //#region Methods

    @Override
    public OrderDTO placeOrder(Long cartId) {
        try {
            Cart cart = cartService.findById(cartId);
            Optional.ofNullable(cart).orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND, null));

            Order order = createOrder(cart);
            Optional.ofNullable(order).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, null));

            Set<OrderEntry> orderEntries = processCartEntries(cart, order);

            finalizeOrder(order, orderEntries, calculateTotalPrice(orderEntries));

            cleanUpCart(cart, cart.getCartEntries());

            return dTOMappersOrder.mapOrderToDTO(order, cart.getId());

        } catch (IllegalStateException e) {
            logger.error("Error placing order: ", e);
            throw new CustomException(ErrorCode.ORDER_PLACEMENT_FAILED, e);
        }
    }
    
    @Transactional
    @Override
    public OrderDTO getOrderById(Long orderId, Long customerId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, null));

        return dTOMappersOrder.convertToOrderDTO(order);
    }

            

    //#endregion

    //#region Functions


    public Long getCartIdByCustomerId(Long customerId) {
        return cartService.findCartIdByCustomerId(customerId);
    }

    private BigDecimal calculateBasePrice(CartEntry cartEntry) {
        return BigDecimal.valueOf(cartEntry.getProduct().getPrice());
    }
    
    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setCustomer(cart.getCustomer());
        return order;
    }

    private Set<OrderEntry> processCartEntries(Cart cart, Order order) {
        Set<CartEntry> cartEntries = cart.getCartEntries();
        Set<OrderEntry> orderEntries = new HashSet<>();

        for (CartEntry cartEntry : cartEntries) {
            OrderEntry orderEntry = createOrderEntry(cartEntry, order);
            orderEntries.add(orderEntry);

            // Ürün stoklarını güncelle
            updateProductStock(cartEntry);
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

    private void updateProductStock(CartEntry cartEntry) {
        Product product = cartEntry.getProduct();
        long newStock = product.getStock() - cartEntry.getQuantity(); // Stoktan miktarı çıkar
    
        if (newStock < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_STOCK, null); // Stok yetersizse hata fırlat
        }
    
        product.setStock(newStock);
        productService.save(product); // Stok miktarını güncelle ve kaydet
    }
    

    private long calculateTotalPrice(Set<OrderEntry> orderEntries) {
        return orderEntries.stream()
                .mapToLong(orderEntry -> orderEntry.getBasePrice().longValue() * orderEntry.getQuantity())
                .sum();
    }

    private void finalizeOrder(Order order, Set<OrderEntry> orderEntries, long totalPrice) {
        order.setTotalPrice(totalPrice);
        order.setOrderEntries(orderEntries);
        orderRepository.save(order);
    }

    private void cleanUpCart(Cart cart, Set<CartEntry> cartEntries) {
        cartEntryService.deleteAll(cartEntries);
        cartService.deleteById(cart.getId());
    }

    public OrderDTO getOrderByOrderCode(String orderCode) {
        Order order = orderRepository
            .findByOrderCode(orderCode)
            .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, null));

        return dTOMappersOrder.convertToOrderDTO(order);
    }

    @Transactional(readOnly = true)
    public java.util.List<OrderDTO> getOrdersByCustomerId(Long customerId) {
        java.util.List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                     .map(order -> dTOMappersOrder.convertToOrderDTO(order))
                     .collect(Collectors.toList());
    }

    //#endregion
}
