package com.draft.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.OrderEntryRepository;

@Service
public class OrderEntryService {

    @Autowired
    private OrderEntryRepository orderEntryRepository;
    
    public boolean existsByProduct(Product product) {
        return orderEntryRepository.existsByProduct(product);
    }
}
