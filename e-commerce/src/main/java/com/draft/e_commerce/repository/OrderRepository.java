package com.draft.e_commerce.repository;

import java.util.Optional;

import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.draft.e_commerce.model.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderCode(String orderCode);

    java.util.List<Order> findByCustomerId(Long customerId);

    
}
 