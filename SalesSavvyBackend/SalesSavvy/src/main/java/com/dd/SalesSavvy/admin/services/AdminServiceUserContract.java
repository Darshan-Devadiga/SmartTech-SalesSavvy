package com.dd.SalesSavvy.admin.services;

import com.dd.SalesSavvy.Entities.Customer;

public interface AdminServiceUserContract {
    Customer modifyUser(Integer userId, String username, String email, String role);
    Customer getUserById(Integer userId);
}
