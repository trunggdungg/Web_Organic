package com.example.web_organic.service;

import com.example.web_organic.entity.Address;
import com.example.web_organic.entity.User;
import com.example.web_organic.modal.request.CreateAddressRequest;
import com.example.web_organic.repository.AddressRepository;
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    private HttpSession httpSession;
    public List<Address> getAddressByUserId(Integer id) {
        return addressRepository.findByUserId(id);
    }


    public Address createAddress(CreateAddressRequest createAddressRequest) {
        User user =  (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Integer userId = user.getId();

        Integer existingAddressCount = addressRepository.countByUserId(userId);

        // Nếu chưa có địa chỉ nào, mặc định set là địa chỉ chính
        if (existingAddressCount == 0) {
            createAddressRequest.setIsDefault(true);
        } else {
            // Nếu người dùng không chọn mặc định, giữ nguyên
            if (createAddressRequest.getIsDefault() == null) {
                createAddressRequest.setIsDefault(false);
            }

            // Nếu người dùng chọn địa chỉ mới là mặc định
            if (Boolean.TRUE.equals(createAddressRequest.getIsDefault())) {
                // Bỏ địa chỉ mặc định cũ
                addressRepository.unsetDefaultAddresses(userId);
            }
        }
        Address address = Address.builder()
                .fullName(createAddressRequest.getName())
                .phone(createAddressRequest.getPhone())
                .districtId(createAddressRequest.getDistrictId())
                .wardCode(createAddressRequest.getWardId())
                .provinceId(createAddressRequest.getProvinceId())
            .addressSelected(createAddressRequest.getAddressSelected())
                .addressDetail(createAddressRequest.getAddressDetail())
                .isDefault(createAddressRequest.getIsDefault())
                .user(user)
                .build();
        return addressRepository.save(address);
    }

    public Address updateAddress(Integer id, CreateAddressRequest createAddressRequest) {
        User user =  (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Integer userId = user.getId();

        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address not found");
        }

        Integer existingAddressCount = addressRepository.countByUserId(userId);

        // Nếu chưa có địa chỉ nào, mặc định set là địa chỉ chính
        if (existingAddressCount == 0) {
            createAddressRequest.setIsDefault(true);
        } else {
            // Nếu người dùng không chọn mặc định, giữ nguyên
            if (createAddressRequest.getIsDefault() == null) {
                createAddressRequest.setIsDefault(false);
            }

            // Nếu người dùng chọn địa chỉ mới là mặc định
            if (Boolean.TRUE.equals(createAddressRequest.getIsDefault())) {
                // Bỏ địa chỉ mặc định cũ
                addressRepository.unsetDefaultAddresses(userId);
            }
        }

        address.setFullName(createAddressRequest.getName());
        address.setPhone(createAddressRequest.getPhone());
        address.setDistrictId(createAddressRequest.getDistrictId());
        address.setWardCode(createAddressRequest.getWardId());
        address.setProvinceId(createAddressRequest.getProvinceId());
        address.setAddressDetail(createAddressRequest.getAddressDetail());
        address.setIsDefault(createAddressRequest.getIsDefault());
        return addressRepository.save(address);
    }

    public void deleteAddress(Integer id) {
        User user =  (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Integer userId = user.getId();

        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address not found");
        }

        addressRepository.deleteById(id);
    }

    public void setDefaultAddress(Integer id) {
        User user =  (User) httpSession.getAttribute("CURRENT_USER");
        if (user == null) {
            throw new RuntimeException("User not logged in");
        }
        Integer userId = user.getId();

        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Address not found");
        }

        addressRepository.unsetDefaultAddresses(userId);

        address.setIsDefault(true);
        addressRepository.save(address);
    }
}
