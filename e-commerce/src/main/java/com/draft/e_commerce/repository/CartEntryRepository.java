package com.draft.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.draft.e_commerce.model.CartEntry;

public interface  CartEntryRepository extends JpaRepository<CartEntry, Long> {

    @Query("SELECT ce FROM CartEntry ce WHERE ce.cart.id = :cartId")
    Optional<CartEntry> findByCartId(@Param("cartId") Long cartId);
    Optional<CartEntry> findByCartIdAndProductId(Long cartId, Long productId);


    void deleteByCartId(Long cartId);
}
