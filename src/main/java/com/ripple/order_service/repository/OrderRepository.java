package com.ripple.order_service.repository;


import com.ripple.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}