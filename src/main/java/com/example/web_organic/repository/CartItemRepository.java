package com.example.web_organic.repository;

import com.example.web_organic.entity.CartItem;
import com.example.web_organic.entity.ProductVariants;
import com.example.web_organic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndProductVariant(User currentUser, ProductVariants productVariant);

    void deleteByUser(User currentUser);
}
