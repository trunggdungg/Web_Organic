package com.example.web_organic.repository;

import com.example.web_organic.entity.User;
import com.example.web_organic.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Integer> {
    List<WishList> findByUser(User currentUser);

    WishList findByUserAndProductId(User currentUser, Integer productId);
}
