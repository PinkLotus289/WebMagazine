package com.example.restservice.repository;

import com.example.restservice.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.products p WHERE LOWER(p.name) = LOWER(:productName)")
    List<Order> findOrdersByProductName(@Param("productName") String productName);
}



