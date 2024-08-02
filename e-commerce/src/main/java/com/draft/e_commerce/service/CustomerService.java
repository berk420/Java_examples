package com.draft.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.repository.CustomerRepository;


@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public java.util.List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}