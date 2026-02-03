package com.dd.SalesSavvy.admin.services;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.Role;
import com.dd.SalesSavvy.customer.customerRepositories.CustomerRepository;
import com.dd.SalesSavvy.customer.customerRepositories.JWTTokenRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@AllArgsConstructor
public class AdminServicesUser implements  AdminServiceUserContract{

    CustomerRepository customerRepository;
    JWTTokenRepo jwtTokenRepo;

    @Transactional
    public Customer modifyUser(Integer userId, String username, String email, String role) {
        // Check if the user exists
        Optional<Customer> userOptional = customerRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        Customer existingUser = userOptional.get();

        // Update user fields
        if (username != null && !username.isEmpty()) {
            existingUser.setUsername(username);
        }
        if (email != null && !email.isEmpty()) {
            existingUser.setEmail(email);
        }
        if (role != null && !role.isEmpty()) {
            try {
                existingUser.setRole(Role.valueOf(role));
            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException("Invalid role: " + role);
            }
        }
        // Delete associated JWT tokens
        jwtTokenRepo.deleteByUserId(userId);
        // Save updated user
        return customerRepository.save(existingUser);
    }

    public Customer getUserById(Integer userId) {
        return customerRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
