package com.example.web_organic.repository;

import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.Status_Enum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByStatus(Status_Enum statusEnum, Pageable pageable);

    Optional<Order> findOrderById(String orderId);

    List<Order> findByUserAndStatus(User user, Status_Enum statusEnum);

    List<Order> findOrderByIdAndUserAndStatus(String orderId, User user, Status_Enum statusEnum);

    // Thống kê doanh thu
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate >= :startDate AND o.orderDate < :endDate")
    BigDecimal getRevenueBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'COMPLETED' AND o.orderDate >= :startDate AND o.orderDate < :endDate")
    int countOrdersBetween(@Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);

    // Dữ liệu cho biểu đồ doanh thu theo thời gian
    @Query("SELECT DATE_FORMAT(o.orderDate, '%d/%m') AS date, SUM(o.total) AS revenue " +
        "FROM Order o " +
        "WHERE o.status = 'COMPLETED' AND MONTH(o.orderDate) = MONTH(CURRENT_DATE) AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
        "GROUP BY DATE_FORMAT(o.orderDate, '%Y-%m-%d') " +
        "ORDER BY o.orderDate")
    List<Map<String, Object>> getDailyRevenue();

    @Query("SELECT MONTHNAME(o.orderDate) AS month, SUM(o.total) AS revenue " +
        "FROM Order o " +
        "WHERE o.status = 'COMPLETED' AND YEAR(o.orderDate) = YEAR(CURRENT_DATE) " +
        "GROUP BY MONTH(o.orderDate) " +
        "ORDER BY MONTH(o.orderDate)")
    List<Map<String, Object>> getMonthlyRevenue();

    // Dữ liệu cho biểu đồ trạng thái đơn hàng
    @Query("SELECT o.status, COUNT(o) FROM Order o  GROUP BY o.status")
    List<Object[]> getOrderStatusDistributionRaw();

    // Dữ liệu cho bảng đơn hàng gần đây
    @Query(value = """
    SELECT 
        o.id AS id,
        o.full_name AS fullName,
        GROUP_CONCAT(p.name SEPARATOR ', ') AS products,
        o.total AS total,
        o.status AS status,
        DATE_FORMAT(o.order_date, '%d/%m/%Y') AS orderDate
    FROM orders o
    JOIN order_details od ON o.id = od.order_id
    JOIN product_variants pv ON od.product_variant_id = pv.id
    JOIN products p ON pv.product_id = p.id
    GROUP BY o.id
    ORDER BY o.order_date DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Map<String, Object>> getRecentOrders();


    @Query(value = """
    SELECT 
        TRIM(SUBSTRING_INDEX(o.address_select, ',', 1)) AS province,
        COUNT(DISTINCT o.user_id) AS customerCount
    FROM orders o
    WHERE o.status = 'COMPLETED'
      AND o.address_select IS NOT NULL
    GROUP BY province
    ORDER BY customerCount DESC
    """, nativeQuery = true)
    List<Map<String, Object>> getCustomerDistributionByProvince();// Phân phối khách hàng theo tỉnh thành



    @Query(value = """
    SELECT DATE_FORMAT(o.order_date, '%d/%m') AS date, SUM(o.total) AS revenue
    FROM orders o
    WHERE o.status = 'COMPLETED'
      AND o.order_date >= DATE_SUB(CURRENT_DATE(), INTERVAL WEEKDAY(CURRENT_DATE()) DAY)
      AND o.order_date <= CURRENT_DATE()
    GROUP BY DATE_FORMAT(o.order_date, '%Y-%m-%d')
    ORDER BY o.order_date
    """, nativeQuery = true)
    List<Map<String, Object>> getWeeklyRevenue();
    // Lấy doanh thu theo tuần hiện tại


    // Lấy doanh thu theo khoảng thời gian tùy chỉnh
    @Query(value = """
    SELECT DATE_FORMAT(o.order_date, '%d/%m') AS date, SUM(o.total) AS revenue
    FROM orders o
    WHERE o.status = 'COMPLETED'
      AND o.order_date BETWEEN :startDate AND :endDate
    GROUP BY DATE(o.order_date)
    ORDER BY o.order_date
    """, nativeQuery = true)
    List<Map<String, Object>> getCustomDateRangeRevenue(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );


    @Query(value = """
    SELECT MONTHNAME(o.order_date) AS date, SUM(o.total) AS revenue
    FROM orders o
    WHERE o.status = 'COMPLETED'
      AND YEAR(o.order_date) = YEAR(CURDATE())
    GROUP BY MONTH(o.order_date)
    ORDER BY MONTH(o.order_date)
""", nativeQuery = true)
    List<Map<String, Object>> getMonthlyRevenues();


    List<Order> findByStatus(Status_Enum status);

    Object countByStatus(Status_Enum statusEnum);



    @Query("SELECT o FROM Order o WHERE LOWER(o.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Order> findOrderByFullName(@Param("fullName") String fullName);
}
