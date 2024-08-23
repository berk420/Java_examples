package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    Owner findByCompanyName(String companyName);

}