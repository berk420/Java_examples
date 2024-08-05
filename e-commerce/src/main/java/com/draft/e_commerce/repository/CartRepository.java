package com.draft.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.draft.e_commerce.model.Cart;


public interface CartRepository extends JpaRepository<Cart, Long> {

}