package com.example.restservice.service;

import com.example.restservice.cache.OrderCache;
import com.example.restservice.exception.InvalidOrderException;
import com.example.restservice.model.Order;
import com.example.restservice.model.Product;
import com.example.restservice.repository.OrderRepository;
import com.example.restservice.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderCache orderCache;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        OrderCache orderCache) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderCache = orderCache;
    }


    public List<Order> findOrdersByProductName(String productName) {
        if (orderCache.contains(productName)) {
            System.out.println("👉 Данные взяты из кэша для productName: " + productName);
            return orderCache.get(productName);
        } else {
            System.out.println("🔄 Кэш отсутствует. Загружаем из БД для productName: "
                    + productName);
            List<Order> orders = orderRepository.findOrdersByProductName(productName);
            orderCache.put(productName, orders);
            return orders;
        }
    }

    public String clearOrdersCache() {
        String message = "🧹 Очистка кэша заказов...";
        System.out.println(message);
        orderCache.clear();
        return message;
    }


    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Order order) {
        Set<Long> productIds = order.getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        List<Product> productsFromDb = productRepository.findAllById(productIds);

        if (productsFromDb.size() != productIds.size()) {
            throw new InvalidOrderException("Один или несколько продуктов не найдены");
        }

        order.setProducts(new HashSet<>(productsFromDb));
        order.recalculateTotalAmount();
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

    public Optional<Order> addProductToOrder(Long orderId, Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return Optional.empty();
        }

        return orderRepository.findById(orderId).map(order -> {
            order.getProducts().add(productOpt.get());
            order.recalculateTotalAmount();
            return orderRepository.save(order);
        });
    }


    public Optional<Order> removeProductFromOrder(Long orderId, Long productId) {
        return orderRepository.findById(orderId).map(order -> {
            order.getProducts().removeIf(p -> p.getId().equals(productId));
            order.recalculateTotalAmount();
            return orderRepository.save(order);
        });
    }

}