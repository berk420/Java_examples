package com.draft.e_commerce.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.repository.CustomerRepository;
import com.draft.e_commerce.service.interf.CustomerServiceInterface;


@Service
public class CustomerService implements CustomerServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    //#region Dependencies

    @Autowired
    private CustomerRepository customerRepository;

    //#endregion

    //#region Methods
    @Override
    public Customer addCustomer(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (Exception e) {
            logger.error("Error adding customer: ", e);
            throw new CustomException(ErrorCode.CUSTOMER_CREATION_FAILED, e);
        }
    }
 
    @Override
    public java.util.List<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (Exception e) {
            logger.error("Error retrieving customers: ", e);
            throw new CustomException(ErrorCode.CUSTOMER_RETRIEVAL_FAILED, e);
        }
    }
    //#endregion

    //#region Functions

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }
    //#endregion

}
