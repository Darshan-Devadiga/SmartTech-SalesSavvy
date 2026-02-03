package com.dd.SalesSavvy.cart.CartServices;

import com.dd.SalesSavvy.Entities.CartItems;
import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.Entities.Product;
import com.dd.SalesSavvy.Entities.ProductImage;
import com.dd.SalesSavvy.cart.cartRepo.CartRepo;
import com.dd.SalesSavvy.product.repositories.ProductImageRepo;
import com.dd.SalesSavvy.product.repositories.ProductRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class CartService implements CartServiceContract{

    CartRepo cartRepo;
    ProductRepo productRepo;
    ProductImageRepo productImageRepo;


    @Override
    public void addToCart(Customer customer, int productId, int quantity) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
        // Fetch cart item for this userld and productid
        Optional<CartItems> existingItem = cartRepo.findByCustomerAndProduct(customer.getUser_id(), productId);
        if (existingItem.isPresent()) {
            CartItems cartItem = existingItem.get();
            cartItem.setQuantity (cartItem.getQuantity() + quantity);
            cartRepo.save(cartItem);
        } else {
            CartItems newItem = new CartItems(customer, product, quantity);
            cartRepo.save(newItem);
        }
    }

    @Override
    public Map<String, Object> getCartItems(Customer customer) {
        List<CartItems> cartItems = cartRepo.findCartItemsWithProductDetails(customer.getUser_id());

        Map<String, Object> response = new HashMap<>();
        response.put("username", customer.getUsername());
        response.put("role", customer.getRole().name());

        List<Map<String, Object>> products = new ArrayList<>();

        int overallPrice = 0;

        for(CartItems cartItem : cartItems) {
            Map<String, Object> productDetails = new HashMap<>();

            Product product = cartItem.getProduct();

            List<ProductImage>  productImages = productImageRepo.findByProduct_productId(product.getProductId());
            String imageUrl = (productImages!=null && !productImages.isEmpty()) ? productImages.get(0).getImageUrl() : "default-image-url";

            productDetails.put("product_id", product.getProductId());
            productDetails.put("image_url", imageUrl);
            productDetails.put("name", product.getName());
            productDetails.put("description", product.getDescription());
            productDetails.put("price_per_unit", product.getPrice());
            productDetails.put("quantity", cartItem.getQuantity());
            productDetails.put("total_price", cartItem.getQuantity()*product.getPrice().doubleValue());

            products.add(productDetails);

            overallPrice += (int) (cartItem.getQuantity()*product.getPrice().doubleValue());
        }

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", products);
        cart.put("overall_total_price", overallPrice);

        response.put("cart", cart);
        return response;
    }

    @Override
    public void updateCartItemQuantity(Customer customer, int productId, int quantity) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItems> cartItem = cartRepo.findByCustomerAndProduct(customer.getUser_id(), productId);

        if(cartItem.isPresent()) {
            CartItems item = cartItem.get();
            if(quantity == 0) {
                deleteCartItem(customer.getUser_id(), productId);
            } else {
                item.setQuantity(quantity);
                cartRepo.save(item);
            }
        }

    }

    public void deleteCartItem(int userId, int productId) {
        cartRepo.deleteCartItem(userId, productId);
    }

    @Override
    public int getCartItemCount(int userId) {
        return cartRepo.countTotalItems(userId);
    }
}
