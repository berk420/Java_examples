package com.draft.e_commerce.service.interf;

import java.util.List;

import com.draft.e_commerce.model.Customer;

public interface CustomerServiceInterface {
    Customer addCustomer(Customer customer);
    List<Customer> getAllCustomers();
}