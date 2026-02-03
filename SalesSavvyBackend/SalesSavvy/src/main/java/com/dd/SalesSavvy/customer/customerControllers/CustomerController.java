package com.dd.SalesSavvy.customer.customerControllers;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.customer.customerServices.CustomerServiceContract;
import com.dd.SalesSavvy.dtos.CustomerRegisterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class CustomerController {

    private final CustomerServiceContract customerServiceContract;

    public CustomerController(CustomerServiceContract customerServiceContract) {
        this.customerServiceContract = customerServiceContract;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerCustomer(@RequestBody CustomerRegisterDto customerRegisterDto) {
        Customer customer = new Customer(customerRegisterDto.getUsername(), customerRegisterDto.getEmail(), customerRegisterDto.getPassword(), customerRegisterDto.getRole());
        try{
            Customer newCustomer = customerServiceContract.registerCustomer(customer);
            return ResponseEntity.ok(Map.of("message", "Customer registered Successfully", "customer", newCustomer));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
