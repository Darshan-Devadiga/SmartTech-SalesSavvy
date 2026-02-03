package com.dd.SalesSavvy.customer.customerServices;

import com.dd.SalesSavvy.Entities.Customer;

public interface AuthServiceContract {
    Customer authenticate(String username, String password);
    String generateToken(Customer customer);
    boolean validateToken(String token);
    String extractUsername(String token);
    void logout(Customer customer);
}
