package com.dd.SalesSavvy.admin.services;

import com.dd.SalesSavvy.Entities.Category;
import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.Entities.ProductImage;
import com.dd.SalesSavvy.product.repositories.CategoryRepo;
import com.dd.SalesSavvy.product.repositories.ProductImageRepo;
import com.dd.SalesSavvy.product.repositories.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AdminServiceProduct implements AdminServiceProductContract{

    CategoryRepo categoryRepo;
    ProductRepo productRepo;
    ProductImageRepo productImageRepo;

    public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl) {
        // Validate the category
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID");
        }
        // Create and save the product
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setCategory(category.get());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepo.save(product);
        // Create and save the product image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ProductImage productimage = new ProductImage();
            productimage.setProduct(savedProduct);
            productimage.setImageUrl(imageUrl);
            productImageRepo.save(productimage);
        } else {
            throw new IllegalArgumentException("Product image URL cannot be empty");
        }
        return savedProduct;
    }

    public void deleteProduct (Integer productId) {
        // Check if the product exists
        if (!productRepo.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }
        // Delete associated product images
        productImageRepo.deleteByProductId(productId);
        // Delete the product
        productRepo.deleteById(productId);
    }
}
