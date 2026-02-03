package com.dd.SalesSavvy.admin.services;

import com.dd.SalesSavvy.Entities.Order;
import com.dd.SalesSavvy.Entities.OrderItem;
import com.dd.SalesSavvy.payment.Repo.OrderItemRepo;
import com.dd.SalesSavvy.payment.Repo.OrderRepo;
import com.dd.SalesSavvy.product.repositories.ProductRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AdminBusinessService implements AdminBusinessContract {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;

    public Map<String, Object> calculateMonthlyBusiness(int month, int year) {
        List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByMonthAndYear(month, year);
        return calculateBusinessMetrics(successfulOrders);
    }

    public Map<String, Object> calculateDailyBusiness(LocalDate date) {
    List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByDate(date);
    return calculateBusinessMetrics(successfulOrders);
    }

    public Map<String, Object> calculateYearlyBusiness(int year) {
        List<Order> successfulOrders = orderRepo.findSuccessfulOrdersByYear(year);
        return calculateBusinessMetrics(successfulOrders);
    }

    public Map<String, Object> calculateOverallBusiness() {
        List<Order> successfulOrders = orderRepo.findAllByOverallBusiness();
          return calculateBusinessMetrics(successfulOrders);

//        BigDecimal totalBusiness = orderRepo.calculateOverallBusiness();
//        Map<String, Object> response = calculateBusinessMetrics(successfulOrders);
//        response.put("totalBusiness", totalBusiness.doubleValue());
//        return response;
    }

    private Map<String, Object> calculateBusinessMetrics(List<Order> orders) {
        double totalRevenue = 0.0;
        Map<String, Integer> categorySales = new HashMap<>();
        for (Order order : orders) {
            totalRevenue += order.getTotalAmount().doubleValue();
            List<OrderItem> items = orderItemRepo.findByOrderId(order.getOrderId());
            for (OrderItem item : items) {
                String categoryName = productRepo.findCategoryNameByProductId(item.getProductId());
                categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
            }
        }
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("categorySales", categorySales);
        return metrics;
    }
}
