package com.example.web_organic.repository;

import com.example.web_organic.entity.StaffPermission;
import com.example.web_organic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffPermissionRepository extends JpaRepository<StaffPermission, Integer> {
    Optional<StaffPermission> findByUser(User user);
}