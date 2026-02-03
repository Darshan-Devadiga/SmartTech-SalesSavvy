package com.dd.SalesSavvy.payment.Repo;

import com.dd.SalesSavvy.Entities.Order;
import com.dd.SalesSavvy.Entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year AND o.status = 'SUCCESS'")
    List<Order> findSuccessfulOrdersByMonthAndYear(int month, int year);

    @Query("SELECT o FROM Order o WHERE DATE(o.createdAt) = :date AND o.status = 'SUCCESS'")
    List<Order> findSuccessfulOrdersByDate(LocalDate date);

    @Query("SELECT o FROM Order o WHERE YEAR(o.createdAt) = :year AND o.status = 'SUCCESS'")
    List<Order> findSuccessfulOrdersByYear(int year);

//    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'SUCCESS'")
//    BigDecimal calculateOverallBusiness();

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findAllByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.status = 'SUCCESS'")
    List<Order> findAllByOverallBusiness();
}
