package com.dd.SalesSavvy.cart.CartControllers;

import com.dd.SalesSavvy.Entities.Customer;
import com.dd.SalesSavvy.cart.CartServices.CartServiceContract;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    CartServiceContract cartServiceContract;

    public CartController(CartServiceContract cartServiceContract) {
        this.cartServiceContract = cartServiceContract;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        String username = (String) request.get("username");
        int productId = (int) request.get("productId");

        // Handle quantity: Default to 1 if not provided
        int quantity = request.containsKey("quantity") ? (int) request.get("quantity"): 1;

        // Fetch the user using username
        Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");

        // Add the product to the cart
        cartServiceContract.addToCart(customer, productId, quantity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
        Customer customer = (Customer) request.getAttribute("authenticatedCustomer");
        Map<String, Object> cartItems = cartServiceContract.getCartItems(customer);
        return ResponseEntity.ok(cartItems);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");
        int productId = (int) request.get("productId");
        int quantity = (int) request.get("quantity");

        cartServiceContract.updateCartItemQuantity(customer, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request, HttpServletRequest httpServletRequest) {
        int productId = (int) request.get("productId");
        Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");

        cartServiceContract.deleteCartItem(customer.getUser_id(), productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/items/count")
    public ResponseEntity<Integer> getCartItemCount(HttpServletRequest httpServletRequest) {
        Customer customer = (Customer) httpServletRequest.getAttribute("authenticatedCustomer");
        int count = cartServiceContract.getCartItemCount(customer.getUser_id());
        return ResponseEntity.ok(count);
    }
}
