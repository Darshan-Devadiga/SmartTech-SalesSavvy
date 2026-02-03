package com.dd.SalesSavvy.admin.services;

import com.dd.SalesSavvy.Entities.Product;

public interface AdminServiceProductContract {
    Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl);
    void deleteProduct (Integer productId);
}
