package com.dd.SalesSavvy.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne // Changed from @OneToOne (usually many items belong to one customer)
    @JoinColumn(name = "user_id")
    Customer customer;

    @ManyToOne // Changed from @OneToMany
    @JoinColumn(name = "product_id")
    Product product;

    int quantity;

    public CartItems(Customer customer, Product product, int quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
    }
}
