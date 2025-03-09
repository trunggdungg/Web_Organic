package com.example.web_organic.repository;

import com.example.web_organic.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
   List<Review> findByProductId(Integer productId);
}
