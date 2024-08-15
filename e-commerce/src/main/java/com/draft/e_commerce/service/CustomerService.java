package com.draft.e_commerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.repository.CustomerRepository;
import com.draft.e_commerce.service.interf.CustomerServiceInterface;


@Service
public class CustomerService implements CustomerServiceInterface {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public java.util.List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
