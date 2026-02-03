package com.dd.SalesSavvy.customer.customerControllers;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.customer.customerServices.AuthServiceContract;
import com.dd.SalesSavvy.dtos.LoginRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthServiceContract authServiceContract;

    public AuthController(AuthServiceContract authServiceContract) {
        this.authServiceContract = authServiceContract;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try{
            Customer customer = authServiceContract.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            String token = authServiceContract.generateToken(customer);

            ResponseCookie cookie = ResponseCookie.from("authToken", token)
                                                    .httpOnly(true)
                                                    .secure(false)
                                                    .sameSite("Lax").path("/")

                                                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            responseBody.put("role", customer.getRole().toString());
            responseBody.put("username", customer.getUsername());
            return ResponseEntity.ok(responseBody);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
// Retrieve authenticated user from the request
            Customer user = (Customer) request.getAttribute("authenticatedCustomer");
// Delegate logout operation to the service layer
            authServiceContract.logout(user);
// Clear the authentication token cookie
            Cookie cookie = new Cookie("authToken", null);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
// Success response
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Logout successful");
            return ResponseEntity.ok(responseBody);
        } catch (RuntimeException e) {
// Error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Logout failed");
            return ResponseEntity.status (500).body (errorResponse);
        }
    }
}
