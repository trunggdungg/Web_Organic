package com.example.web_organic.repository;

import com.example.web_organic.entity.OrderCancellation;
import com.example.web_organic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCanCellationRepository extends JpaRepository<OrderCancellation, Integer> {
    List<OrderCancellation> findByUser(User user);
}
