package com.draft.e_commerce.controller;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderDTO placeOrder(@RequestParam Long cartId) {
        return orderService.placeOrder(cartId);
    }
    
    @GetMapping("/{orderId}/customer/{customerId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable("orderId") Long orderId, @PathVariable("customerId") Long customerId) {
    
        OrderDTO order = orderService.getOrderById(orderId, customerId);
    
        if (order == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(order);
        }
    }

    @GetMapping("/orderCode/{orderCode}")
    public ResponseEntity<OrderDTO> getOrderByOrderCode(@PathVariable("orderCode") String orderCode) {
        OrderDTO order = orderService.getOrderByOrderCode(orderCode);

        if (order == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(order);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<java.util.List<OrderDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        java.util.List<OrderDTO> orders = orderService.getOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content döndür
        } else {
            return ResponseEntity.ok(orders); // 200 OK ve listeyi döndür
        }
    }
}
