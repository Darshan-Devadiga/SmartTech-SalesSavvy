package com.dd.SalesSavvy.customer.customerServiceImplementations;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.customer.customerRepositories.CustomerRepository;
import com.dd.SalesSavvy.customer.customerServices.CustomerServiceContract;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements CustomerServiceContract {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Customer registerCustomer(Customer customer) {

//        Optional<Customer> us = customerRepository.findByUsername(customer.getUsername());
//        if(us.isPresent()) throw new RuntimeException("Username is already taken");
        if(customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }

        if(customerRepository.findByEmail(customer.getEmail()).isPresent()){
            throw new RuntimeException("Email is already registered");
        }

//        String pwd = passwordEncoder.encode(customer.getPassword());
//        customer.setPassword(pwd);
        customer.setPassword(passwordEncoder().encode(customer.getPassword()));
        return customerRepository.save(customer);
    }


}
