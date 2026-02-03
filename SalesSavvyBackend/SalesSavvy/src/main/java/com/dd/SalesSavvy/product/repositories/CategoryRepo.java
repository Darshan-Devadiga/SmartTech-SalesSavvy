package com.dd.SalesSavvy.product.repositories;

import com.dd.SalesSavvy.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String categoryName);
}
