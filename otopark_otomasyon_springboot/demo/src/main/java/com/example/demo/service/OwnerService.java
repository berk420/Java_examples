package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Owner;
import com.example.demo.repository.OwnerRepository;

@Service
public class OwnerService {
    @Autowired
    private OwnerRepository ownerRepository;

    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    public Owner setCarNumber(String companyname, int capacity) {
        // Önce mevcut owner'ı kontrol et
        Owner existingOwner = ownerRepository.findByCompanyName(companyname);

        if (existingOwner != null) {
            // Eğer mevcutsa, güncelleme işlemi yap
            existingOwner.setCapacity(capacity);
            return ownerRepository.save(existingOwner);
        } else {
            // Eğer mevcut değilse, yeni bir owner oluştur
            Owner newOwner = new Owner(companyname, 10, capacity);
            return ownerRepository.save(newOwner);
        }
    }
}
