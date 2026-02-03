package com.dd.SalesSavvy.cart.cartRepo;

import com.dd.SalesSavvy.Entities.CartItems;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<CartItems, Integer> {
    @Query("SELECT c FROM CartItems c WHERE c.customer.user_id = :userid AND c.product.productId = :productid")
    Optional<CartItems> findByCustomerAndProduct(int userid, int productid);

    @Query("SELECT c FROM CartItems c JOIN FETCH c.product p LEFT JOIN FETCH ProductImage pi ON p.productId = pi.product.productId WHERE c.customer.user_id = :userId")
    List<CartItems> findCartItemsWithProductDetails(int userId);


    @Modifying
    @Transactional
    @Query("DELETE FROM CartItems c WHERE c.customer.user_id = :userId AND c.product.productId = :productId")
    void deleteCartItem(int userId, int productId);

    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItems c WHERE c.customer.user_id = :userId")
    int countTotalItems(int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItems c WHERE c.customer.user_id = :userId")
    void deleteAllCartItemsByUserId(int userId);

}
