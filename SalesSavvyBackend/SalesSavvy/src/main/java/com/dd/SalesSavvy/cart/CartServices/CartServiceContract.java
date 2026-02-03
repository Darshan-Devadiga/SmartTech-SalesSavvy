package com.dd.SalesSavvy.cart.CartServices;

import com.dd.SalesSavvy.Entities.Customer;

import java.util.Map;

public interface CartServiceContract {
    void addToCart(Customer customer, int productId, int quantity);
    Map<String, Object> getCartItems(Customer customer);
    void updateCartItemQuantity(Customer customer, int productId, int quantity);
    void deleteCartItem(int userId, int productId);
    int getCartItemCount(int userId);
}
