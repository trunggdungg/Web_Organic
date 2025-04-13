package com.example.web_organic.modal.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class DashboardStatsResponse {
    BigDecimal currentMonthRevenue;
    BigDecimal previousMonthRevenue;
    Double revenueGrowthRate;

    Integer currentMonthOrders;
    Integer previousMonthOrders;
    Double orderGrowthRate;

    Integer totalCustomers;
    Integer newCustomersCurrentMonth;
    Integer newCustomersPreviousMonth;
    Double customerGrowthRate;

    Integer hotProductsCurrentMonth;
    Integer hotProductsPreviousMonth;
    Double hotProductGrowthRate;
}
