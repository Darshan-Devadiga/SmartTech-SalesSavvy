package com.dd.SalesSavvy.admin.controller;

import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.admin.services.AdminServiceProductContract;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/admin/products")
public class AdminProductController {
    AdminServiceProductContract adminServiceProductContract;

    public AdminProductController(AdminServiceProductContract adminServiceProductContract) {
        this.adminServiceProductContract = adminServiceProductContract;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> productRequest) {
        try {
            String name = (String) productRequest.get("name");
            String description = (String) productRequest.get("description");
            Double price = Double.valueOf(String.valueOf(productRequest.get("price")));
            Integer stock = (Integer) productRequest.get("stock");
            Integer categoryId = (Integer) productRequest.get("categoryId");
            String imageUrl = (String) productRequest.get("imageUrl");
            Product addedProduct = adminServiceProductContract.addProductWithImage(name, description, price, stock, categoryId, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body("added Product");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Integer> requestBody) {
        try {
            Integer productId = requestBody.get("productId");
            adminServiceProductContract.deleteProduct(productId);
            return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
