package com.dd.SalesSavvy.product.productService;

import com.dd.SalesSavvy.Entities.Product;

import java.util.List;

public interface ProductServiceContract {
    List<Product> getProductsByCategory(String categoryName);
    List<String> getProductImages(int productId);
}
