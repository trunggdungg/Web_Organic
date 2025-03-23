package com.example.web_organic.service;
import com.example.web_organic.entity.Brand;
import com.example.web_organic.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public List<Brand> getBrandByStatus() {
        return brandRepository.findByStatus(true);
    }
}
