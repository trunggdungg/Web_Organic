package com.example.web_organic.service;
import com.example.web_organic.entity.Product;
import com.example.web_organic.entity.Review;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.CreateReviewRequest;
import com.example.web_organic.repository.ProductRepository;
import com.example.web_organic.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ProductRepository productRepository;
    public Review createReview(CreateReviewRequest createReviewRequest) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }
        Product product = productRepository.findById(createReviewRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        // Create review
        Review review = Review.builder()
                .content(createReviewRequest.getContent())
                .createdAt(LocalDateTime.now())
                .rating(createReviewRequest.getRating())
                .updatedAt(LocalDateTime.now())
                .product(product)
                .user(currentUser)
                .build();
            return reviewRepository.save(review);

    }

    public Review updateReview(Integer id, CreateReviewRequest createReviewRequest) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this review");
        }
        review.setContent(createReviewRequest.getContent());
        review.setRating(createReviewRequest.getRating());
        review.setUpdatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    public void deleteReview(Integer id) {
        User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
        if (currentUser == null) {
            throw new RuntimeException("User not logged in");
        }
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not the owner of this review");
        }
        reviewRepository.delete(review);
    }
}
