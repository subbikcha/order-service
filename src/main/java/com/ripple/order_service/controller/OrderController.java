package com.ripple.order_service.controller;

import com.ripple.order_service.entity.Order;
import com.ripple.order_service.service.OrderService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/{userId}")
    public Order createOrder(@PathVariable String userId) {
        return service.createOrder(userId);
    }


    // GET ALL ORDERS
    @GetMapping
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    // GET ORDER BY ID
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return service.getOrderById(orderId);
    }
}