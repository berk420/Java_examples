package com.draft.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.draft.e_commerce.model.OrderEntry;

@Repository
public interface OrderEntryRepository extends JpaRepository<OrderEntry, Long> {
}
