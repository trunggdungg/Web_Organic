package com.example.web_organic.rest;

import com.example.web_organic.entity.Address;
import com.example.web_organic.modal.request.CreateAddressRequest;
import com.example.web_organic.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressApi {
    @Autowired
    private AddressService addressService;
    @PostMapping("/create")
    public ResponseEntity<?> createAddress(@RequestBody CreateAddressRequest createAddressRequest) {
        Address address = addressService.createAddress(createAddressRequest);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer id, @RequestBody CreateAddressRequest createAddressRequest) {
        Address address = addressService.updateAddress(id, createAddressRequest);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok("Delete address successfully");
    }

    @PutMapping("/setDefault/{id}")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Integer id) {
        addressService.setDefaultAddress(id);
        return ResponseEntity.ok("Set default address successfully");
    }
}
