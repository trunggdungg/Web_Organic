package com.example.web_organic.repository;

import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.Status_Enum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findAllByStatus(Status_Enum statusEnum, Pageable pageable);

    Optional<Order> findOrderById(String orderId);

    List<Order> findByUserAndStatus(User user, Status_Enum statusEnum);

    List<Order> findOrderByIdAndUserAndStatus(String orderId, User user, Status_Enum statusEnum);
}
