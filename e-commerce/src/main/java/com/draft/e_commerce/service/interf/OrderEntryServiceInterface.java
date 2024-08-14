package com.draft.e_commerce.service.interf;

import java.util.List;
import java.util.Optional;

import com.draft.e_commerce.model.OrderEntry;

public interface OrderEntryServiceInterface {
    OrderEntry createOrderEntry(OrderEntry orderEntry);
    Optional<OrderEntry> getOrderEntry(Long id);
    List<OrderEntry> getAllOrderEntries();
    OrderEntry updateOrderEntry(OrderEntry orderEntry);
    void deleteOrderEntry(Long id);
}