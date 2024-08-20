package com.draft.e_commerce.controller;

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
    
    @GetMapping("{orderId}/customer/{customerId}")    
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long OrderId,@PathVariable Long CustomerId) {
        OrderDTO order = orderService.getOrderById(OrderId,CustomerId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(order);
        }
    }
}
