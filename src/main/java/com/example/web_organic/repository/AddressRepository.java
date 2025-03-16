package com.example.web_organic.repository;

import com.example.web_organic.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    Optional<Object> findByUserIdAndIsDefaultTrue(Integer id);

    @Query("SELECT a.addressSelected FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    String findAddressSelectedByUserId(@Param("userId") Integer userId);


    List<Address> findByIsDefaultTrue();

    List<Address> findByUserId(Integer id);
}
