package com.dd.SalesSavvy.payment.controller;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.OrderItem;
import com.dd.SalesSavvy.payment.services.PaymentServiceContract;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
@AllArgsConstructor
public class PaymentController {

    PaymentServiceContract paymentServiceContract;

    @PostMapping("/create")
    public ResponseEntity<String> createPaymentOrder(@RequestBody Map<String, Object> requestBody, HttpServletRequest httpServletRequest) {
        try {
            Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");

            BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
            List<Map<String, Object>> cartItemsRaw = (List<Map<String, Object>>) requestBody.get("cartItems");

            // Convert cartitemsRaw to List<Orderltem>
            List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId ((Integer) item.get("productId"));
                orderItem.setQuantity ((Integer) item.get("quantity"));
                BigDecimal pricePerUnit = new BigDecimal (item.get("price").toString());
                orderItem.setPricePerUnit(pricePerUnit);
                orderItem.setTotalPrice (pricePerUnit.multiply(BigDecimal.valueOf((Integer) item.get("quantity"))));
                return orderItem;
            }).collect(Collectors.toList());

            String razorpayOrderId = paymentServiceContract.createOrder(customer.getUser_id(), totalAmount, cartItems);
            return ResponseEntity.ok(razorpayOrderId);
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating razorpay order: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data: "+ e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> requestBody, HttpServletRequest httpServletRequest) {
        try{
            Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");
            int userId = customer.getUser_id();

            String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
            String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
            String razorpaySignature = (String) requestBody.get("razorpaySignature");

            boolean isVerified  = paymentServiceContract.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature, userId);

            if(isVerified) return ResponseEntity.ok("Payment verifies Successfully");
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment" + e.getMessage());
        }
    }
}
