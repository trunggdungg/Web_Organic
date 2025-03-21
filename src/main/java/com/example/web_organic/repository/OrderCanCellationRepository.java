package com.example.web_organic.repository;

import com.example.web_organic.entity.OrderCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCanCellationRepository extends JpaRepository<OrderCancellation, Integer> {
}
