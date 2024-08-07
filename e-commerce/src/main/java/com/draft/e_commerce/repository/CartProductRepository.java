package com.draft.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.draft.e_commerce.model.CartProduct;
import com.draft.e_commerce.model.CartProductId;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {
    //List<CartProduct> findByIdCartId(Long cartId);
    Optional<CartProduct> findByIdCartId(Long cartId);
    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);


    @Modifying
    @Query("DELETE FROM cart_product cp WHERE cp.cart.id = :cartId")
    void deleteByCartId(Long cartId);
}
