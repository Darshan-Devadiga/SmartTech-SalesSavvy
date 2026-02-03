package com.dd.SalesSavvy.filters;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.Role;
import com.dd.SalesSavvy.customer.customerRepositories.CustomerRepository;
import com.dd.SalesSavvy.customer.customerServices.AuthServiceContract;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter(urlPatterns = {"/api/*", "/admin/*"})
@Component
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final AuthServiceContract authServiceContract;
    private final CustomerRepository customerRepository;

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    private static final String[]  UNAUTHORIZED_PATHS = {
            "/api/register",
            "/api/auth/login"
    };

    public AuthenticationFilter(AuthServiceContract authServiceContract, CustomerRepository customerRepository) {
        logger.info("FILTER: Filter started to execute");
        this.authServiceContract = authServiceContract;
        this.customerRepository = customerRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try{
            executeFilterLogicChain(request, response, chain);
        } catch(Exception e) {
            logger.error("Unexcepted Error in Authentication Filter", e);
            sendErrorResponse((HttpServletResponse) response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Internal Server Error");
        }
    }

    private void executeFilterLogicChain(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestURI = httpServletRequest.getRequestURI();
        logger.info("Request Uri: {}", requestURI);

        //Allow Unauthenticated Paths
        if (Arrays.asList(UNAUTHORIZED_PATHS).contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        // Handle Preflight (OPTIONS) Requests
        if(httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")){
            setCORSHeader(httpServletResponse);
            return;
        }

        // Extract and Validate the Token
        String token = getAuthTokenFromCookies(httpServletRequest);
        logger.info("Token extracted: ", token);
        if(token == null || !authServiceContract.validateToken(token)) {
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: customer not found");
            return;
        }

        // Extract username & verify customer
        String username = authServiceContract.extractUsername(token);
        Optional<Customer> customerOptional = customerRepository.findByUsername(username);

        if(customerOptional.isEmpty()) {
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "Forbidden: Admin Access required");
            return;
        }

        // Get Authenticated customer and role
        Customer authenticatedCustomer = customerOptional.get();
        Role role = authenticatedCustomer.getRole();
        logger.info("Authenticated Customer: {}, Role: {}", authenticatedCustomer.getUsername(), role);

        // Role Based Access Control
        if(requestURI.startsWith("/api/") && role != Role.CUSTOMER) {
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_FORBIDDEN, "Customer access required");
            return;
        }

        if(requestURI.startsWith("/admin/") && role != Role.ADMIN) {
            sendErrorResponse(httpServletResponse, HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        // Attach Customer details to request
        httpServletRequest.setAttribute("authenticatedCustomer",authenticatedCustomer);
        chain.doFilter(request, response);

    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }

    private void setCORSHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Method","GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private String getAuthTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "authToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
