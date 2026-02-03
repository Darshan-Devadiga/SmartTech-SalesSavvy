package com.dd.SalesSavvy.customer.customerRepositories;

import com.dd.SalesSavvy.Entities.JWTToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JWTTokenRepo extends JpaRepository<JWTToken, Integer> {

    @Query("SELECT t FROM JWTToken t WHERE t.customer.user_id = :userId")
    JWTToken findByUserId(@Param("userId") int userId);
    Optional<JWTToken> findByToken(String token);

    // Custom query to delete tokens by user ID
    @Modifying
    @Transactional
    @Query("DELETE FROM JWTToken t WHERE t.customer.user_id = :userId")
    void deleteByUserId(@Param("userId") int userId);
}
