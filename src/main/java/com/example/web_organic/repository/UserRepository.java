package com.example.web_organic.repository;

import com.example.web_organic.entity.User;
import com.example.web_organic.modal.Enum.User_Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
//    Optional<Object> findByEmail(String email);
    Optional<User> findByEmailAndIsActivatedTrue(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u")
    Page<User> findAllUser(Pageable pageable);

    User findUserById(Integer id);

    @Query("SELECT COUNT(c) FROM User c WHERE c.createdAt >= :startDate AND c.createdAt < :endDate")
    int countNewCustomersBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    //khách hàng mới
    List<User> findTop5AndStatusTrueByOrderByCreatedAtDesc();

   User findByRole(User_Role userRole);
}
