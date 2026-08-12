package com.medifind.backend.repository;

import com.medifind.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByPlacedAtDesc(Long customerId);
}