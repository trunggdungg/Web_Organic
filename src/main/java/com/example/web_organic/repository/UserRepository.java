package com.example.web_organic.repository;

import com.example.web_organic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<Object> findByEmail(String email);
    Optional<User> findByEmailAndIsActivatedTrue(String email);

    Optional<User> findByEmail(String email);
}
