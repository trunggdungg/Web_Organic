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

    public Brand createBrand(String nameBrand, String imageUrl, Boolean status) {
        Brand brand = new Brand();
        brand.setNameBrand(nameBrand);
        brand.setLogo(imageUrl);
        brand.setStatus(status);
        brand.setSlug(nameBrand.toLowerCase().replaceAll(" ", "-"));
        return brandRepository.save(brand);
    }

    public Brand updateBrand(Integer id, String nameBrand, String imageUrl, Boolean status) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
        brand.setNameBrand(nameBrand);
        if (imageUrl != null) {
            brand.setLogo(imageUrl);
        }
        brand.setStatus(status);
        brand.setSlug(nameBrand.toLowerCase().replaceAll(" ", "-"));
        return brandRepository.save(brand);
    }

    public void deleteBrand(Integer id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
        brandRepository.delete(brand);
    }
}
