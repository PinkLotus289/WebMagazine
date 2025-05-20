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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

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
            logger.info("👉 Данные взяты из кэша для productName: {}", productName);
            return orderCache.get(productName);
        } else {
            logger.info("🔄 Кэш отсутствует. Загружаем из БД для productName: {}", productName);
            List<Order> orders = orderRepository.findOrdersByProductName(productName);
            orderCache.put(productName, orders);
            return orders;
        }
    }

    public String clearOrdersCache() {
        String message = "🧹 Очистка кэша заказов...";
        logger.info(message);
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

        productsFromDb.forEach(p -> orderCache.invalidate(p.getName()));

        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Long id, Order updatedOrder) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setCustomerName(updatedOrder.getCustomerName());

                    // 💥 Заменяем сырые продукты на полные объекты из БД
                    Set<Long> productIds = updatedOrder.getProducts().stream()
                            .map(p -> p.getId())
                            .collect(Collectors.toSet());

                    Set<Product> productsFromDb = new HashSet<>(productRepository.findAllById(productIds));
                    order.setProducts(productsFromDb);

                    order.recalculateTotalAmount();

                    productsFromDb.forEach(p -> orderCache.invalidate(p.getName()));
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
            orderCache.invalidate(productOpt.get().getName());
            return orderRepository.save(order);
        });
    }

    public Optional<Order> removeProductFromOrder(Long orderId, Long productId) {
        return orderRepository.findById(orderId).map(order -> {
            Optional<Product> toRemove = order.getProducts().stream()
                    .filter(p -> p.getId().equals(productId))
                    .findFirst();

            boolean removed = order.getProducts().removeIf(p -> p.getId().equals(productId));
            if (removed) {
                toRemove.ifPresent(p -> orderCache.invalidate(p.getName()));
            }

            order.recalculateTotalAmount();
            return orderRepository.save(order);
        });
    }
}
