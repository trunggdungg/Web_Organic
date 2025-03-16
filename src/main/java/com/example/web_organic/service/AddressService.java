package com.example.web_organic.service;

import com.example.web_organic.entity.Address;
import com.example.web_organic.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;
    public List<Address> getAddressByUserId(Integer id) {
        return addressRepository.findByUserId(id);
    }


}
