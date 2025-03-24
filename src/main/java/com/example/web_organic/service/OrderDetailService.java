package com.example.web_organic.service;
import com.example.web_organic.entity.Order;
import com.example.web_organic.entity.OrderDetail;
import com.example.web_organic.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;


    public List<OrderDetail> getAll() {
        return orderDetailRepository.findAll();
    }

    public List<OrderDetail> getOrderDetailsByOrderId(String id) {
        return orderDetailRepository.findByOrderId(id);
    }
}
