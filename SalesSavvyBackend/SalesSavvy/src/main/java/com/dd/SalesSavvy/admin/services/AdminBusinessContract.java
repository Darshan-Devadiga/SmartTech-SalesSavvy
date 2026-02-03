package com.dd.SalesSavvy.admin.services;

import java.time.LocalDate;
import java.util.Map;

public interface AdminBusinessContract {
    Map<String, Object> calculateMonthlyBusiness(int month, int year);
    Map<String, Object> calculateDailyBusiness(LocalDate date);
    Map<String, Object> calculateYearlyBusiness(int year);
    Map<String, Object> calculateOverallBusiness();
}
