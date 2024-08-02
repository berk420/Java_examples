package com.draft.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.draft.e_commerce.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
