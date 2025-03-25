package com.example.web_organic.rest;

import com.example.web_organic.entity.Review;
import com.example.web_organic.modal.request.CreateReviewRequest;
import com.example.web_organic.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewApi {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest createReviewRequest) {
        try {
            Review review = reviewService.createReview(createReviewRequest);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Integer id, @RequestBody CreateReviewRequest createReviewRequest) {
        try {
            Review review = reviewService.updateReview(id, createReviewRequest);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
