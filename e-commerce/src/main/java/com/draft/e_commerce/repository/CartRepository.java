package com.draft.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.draft.e_commerce.model.Cart;


public interface CartRepository extends JpaRepository<Cart, Long> {
    
    @Query("SELECT c.id FROM Cart c WHERE c.customer.id = :customerId")
    Long findCartIdByCustomerId(@Param("customerId") Long customerId);

}