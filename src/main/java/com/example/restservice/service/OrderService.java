package com.example.restservice.service;

import com.example.restservice.model.Order;
import com.example.restservice.repository.OrderRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final Map<String, List<Order>> ordersByProductNameCache;

    public OrderService(OrderRepository orderRepository, Map<String,
            List<Order>> ordersByProductNameCache) {
        this.orderRepository = orderRepository;
        this.ordersByProductNameCache = ordersByProductNameCache;
    }

    public List<Order> getOrdersByProductName(String productName) {
        return ordersByProductNameCache.computeIfAbsent(productName,
                orderRepository::findOrdersByProductName
        );
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> findOrdersByProductName(String productName) {
        return orderRepository.findOrdersByProductName(productName);
    }

    public Order createOrder(Order order) {
        order.recalculateTotalAmount(); // Пересчитать итоговую сумму
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setCustomerName(updatedOrder.getCustomerName());
                    order.setProducts(updatedOrder.getProducts());
                    order.recalculateTotalAmount(); // Пересчитать сумму после изменения продуктов
                    return orderRepository.save(order);
                });
    }

    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}