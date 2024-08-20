package com.draft.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.draft.e_commerce.model.Order;


public interface OrderRepository extends JpaRepository<Order, Long> {
}
