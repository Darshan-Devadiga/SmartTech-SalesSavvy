package com.dd.SalesSavvy.payment.services;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.OrderItem;
import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.Entities.ProductImage;
import com.dd.SalesSavvy.payment.Repo.OrderItemRepo;
import com.dd.SalesSavvy.payment.Repo.OrderRepo;
import com.dd.SalesSavvy.product.repositories.ProductImageRepo;
import com.dd.SalesSavvy.product.repositories.ProductRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrderService implements OrderServiceContract{

    OrderItemRepo orderItemRepo;
    ProductRepo productRepo;
    ProductImageRepo productImageRepo;

    public Map<String, Object> getOrdersForUser(Customer customer) {
// Fetch all successful order items for the user
        List<OrderItem> orderItems = orderItemRepo.findSuccessfulOrderItemsByUserId(customer.getUser_id());
// Prepare the response map
        Map<String, Object> response = new HashMap<>();
        response.put("username", customer.getUsername());
        response.put("role", customer.getRole()); // Directly use the role as it is an enum mapped to a string
// Transform order items into a list of product details
        List<Map<String, Object>> products = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Product product = productRepo.findById(item.getProductId()).orElse(null);
            if (product == null) {
                continue; // Skip if the product does not exist
            }

// Fetch the product image (if available)
            List<ProductImage> images = productImageRepo.findByProduct_productId(product.getProductId());
            String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
// Create a product details map
            Map<String, Object> productDetails = new HashMap<>();
            productDetails.put("order_id", item.getOrder().getOrderId());
            productDetails.put("quantity", item.getQuantity());
            productDetails.put("total_price", item.getTotalPrice());
            productDetails.put("image_url", imageUrl);
            productDetails.put("product_id", product.getProductId());
            productDetails.put("name", product.getName());
            productDetails.put("description", product.getDescription());
            productDetails.put("price_per_unit", item.getPricePerUnit());
            products.add(productDetails);
        }
// Add the products list to the response
        response.put("products", products);
        return response;
    }
}
