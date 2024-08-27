package com.draft.e_commerce.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartEntryRepository;

@Service
public class CartEntryService {

    @Autowired
    private CartEntryRepository cartEntryRepository;

    public void deleteAll(Set<CartEntry> cartEntries) {
        cartEntryRepository.deleteAll(cartEntries);
    }

    public boolean existsByProduct(Product product) {
        return cartEntryRepository.existsByProduct(product);
    }

}
