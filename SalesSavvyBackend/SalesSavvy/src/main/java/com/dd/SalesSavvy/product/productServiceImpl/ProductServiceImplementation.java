package com.dd.SalesSavvy.product.productServiceImpl;

import com.dd.SalesSavvy.Entities.Category;
import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.Entities.ProductImage;
import com.dd.SalesSavvy.product.productService.ProductServiceContract;
import com.dd.SalesSavvy.product.repositories.CategoryRepo;
import com.dd.SalesSavvy.product.repositories.ProductImageRepo;
import com.dd.SalesSavvy.product.repositories.ProductRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImplementation implements ProductServiceContract {

    CategoryRepo categoryRepo;
    ProductRepo productRepo;
    ProductImageRepo productImageRepo;


    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        if(categoryName != null && !categoryName.isEmpty()) {
            Optional<Category> categoryOptional = categoryRepo.findByCategoryName(categoryName);
            if(categoryOptional.isPresent()) {
                Category category = categoryOptional.get();
                return productRepo.findByCategory_CategoryId(category.getCategoryId());
            } else {
                throw new RuntimeException("Category not found");
            }
        } else {
            return productRepo.findAll();
        }
    }

    @Override
    public List<String> getProductImages(int productId) {
        List<ProductImage> productImages = productImageRepo.findByProduct_productId(productId);
        List<String> imageUrls = new ArrayList<>();
        for(ProductImage image : productImages){
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
}
