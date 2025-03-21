package com.example.web_organic.service;
import com.example.web_organic.entity.OrderCancellation;
import com.example.web_organic.entity.User;
import com.example.web_organic.repository.OrderCanCellationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCanCellationService {
    @Autowired
    private OrderCanCellationRepository orderCancellationRepository;
    public List<OrderCancellation> getAllByUser(User user) {
        return orderCancellationRepository.findByUser(user);
    }
}
