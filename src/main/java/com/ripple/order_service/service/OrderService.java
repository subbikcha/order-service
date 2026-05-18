package com.ripple.order_service.service;

import com.ripple.order_service.dto.UserDTO;
import com.ripple.order_service.entity.Order;
import com.ripple.order_service.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public Order createOrder(String userId) {

        // CALL USER SERVICE
        UserDTO user = restTemplate.getForObject(
                "http://localhost:8081/users/" + userId,
                UserDTO.class
        );

        int deliveryFee = 50;

        // IMPORTANT BUSINESS LOGIC
        if(user.getTier().equals("premium")) {
            deliveryFee = 0;
        }

        Order order = new Order();

        order.setUserId(user.getUserId());
        order.setUserName(user.getUserName());
        order.setTier(user.getTier());
        order.setTotalAmount(500);
        order.setDeliveryFee(deliveryFee);

        return repository.save(order);
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order getOrderById(String orderId) {
        return repository.findById(orderId).orElse(null);
    }
}