package com.dd.SalesSavvy.payment.services;

import com.dd.SalesSavvy.Entities.Order;
import com.dd.SalesSavvy.Entities.OrderItem;
import com.razorpay.RazorpayException;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentServiceContract {
    String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException;
    boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, int userId);
}
