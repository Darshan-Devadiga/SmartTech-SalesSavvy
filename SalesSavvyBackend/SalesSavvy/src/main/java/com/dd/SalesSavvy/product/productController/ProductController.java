package com.dd.SalesSavvy.product.productController;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.product.productService.ProductServiceContract;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    ProductServiceContract productServiceContract;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(@RequestParam(required = false) String category, HttpServletRequest request) {
        try{
            // Retrieve Authenticated user from request attribute set by the filter
            Customer authenticatedUser = (Customer) request.getAttribute("authenticatedCustomer");
            if(authenticatedUser == null) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(Map.of("error", "Unauthorized access"));
            }

            //Fetch products based on category filter
            List<Product> products = productServiceContract.getProductsByCategory(category);

            // Build the response
            Map<String, Object> response = new HashMap<>();

            // Add userInfo
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("name", authenticatedUser.getUsername());
            userInfo.put("role", authenticatedUser.getRole().name());
            response.put("user", userInfo);

            // Add product Details
            List<Map<String, Object>> productList = new ArrayList<>();
            for(Product product : products) {
                Map<String, Object> productDetails = new HashMap<>();
                productDetails.put("product_id", product.getProductId());
                productDetails.put("name", product.getName());
                productDetails.put("description", product.getDescription());
                productDetails.put("price", product.getPrice());
                productDetails.put("stock", product.getStock());

                //Fetch Product image
                List<String> images = productServiceContract.getProductImages(product.getProductId());
                productDetails.put("images", images);
                productList.add(productDetails);
            }
            response.put("products", productList);
            return  ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
