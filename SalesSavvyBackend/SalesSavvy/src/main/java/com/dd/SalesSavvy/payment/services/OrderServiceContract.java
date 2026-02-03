package com.dd.SalesSavvy.payment.services;

import com.dd.SalesSavvy.Entities.Customer;

import java.util.Map;

public interface OrderServiceContract {
    Map<String, Object> getOrdersForUser(Customer customer);
}
