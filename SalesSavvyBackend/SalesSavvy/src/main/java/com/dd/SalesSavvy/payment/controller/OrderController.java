package com.dd.SalesSavvy.payment.controller;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.payment.services.OrderServiceContract;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    OrderServiceContract orderServiceContract;

    public OrderController(OrderServiceContract orderServiceContract) {
        this.orderServiceContract = orderServiceContract;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrdersForUser(HttpServletRequest request) {
        try {
// Retrieve the authenticated user from the request
            Customer authenticatedUser = (Customer) request.getAttribute("authenticatedCustomer");
// Fetch orders for the user via the service layer
            Map<String, Object> response = orderServiceContract.getOrdersForUser(authenticatedUser);
// Return the response with HTTP 200 OK
            return ResponseEntity.ok (response);
        } catch (IllegalArgumentException e) {
// Handle cases where user details are invalid or missing
            return ResponseEntity.status(400).body (Map.of("error", e.getMessage()));
        } catch (Exception e) {
// Handle unexpected exceptions
            e.printStackTrace();
            return ResponseEntity.status (500).body (Map.of("error", "An unexpected error occurred"));
        }
    }
}
