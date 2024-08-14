package com.draft.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.OrderEntry;
import com.draft.e_commerce.repository.OrderEntryRepository;
import com.draft.e_commerce.service.interf.OrderEntryServiceInterface;

@Service
public class OrderEntryService implements OrderEntryServiceInterface{

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    public OrderEntry createOrderEntry(OrderEntry orderEntry) {
        return orderEntryRepository.save(orderEntry);
    }

    public Optional<OrderEntry> getOrderEntry(Long id) {
        return orderEntryRepository.findById(id);
    }

    public List<OrderEntry> getAllOrderEntries() {
        return orderEntryRepository.findAll();
    }

    public OrderEntry updateOrderEntry(OrderEntry orderEntry) {
        return orderEntryRepository.save(orderEntry);
    }

    public void deleteOrderEntry(Long id) {
        orderEntryRepository.deleteById(id);
    }
}
