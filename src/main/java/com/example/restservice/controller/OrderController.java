package com.example.restservice.controller;

import com.example.restservice.model.Order;
import com.example.restservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
@Tag(name = "Заказы", description = "Операции с заказами")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Получить все заказы")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/by-product-name")
    @Operation(summary = "Получить заказы по названию продукта")
    public List<Order> getOrdersByProductName(@RequestParam String productName) {
        return orderService.findOrdersByProductName(productName);
    }

    @DeleteMapping("/cache/clear")
    @Operation(summary = "Очистить кэш заказов")
    public ResponseEntity<String> clearOrdersCache() {
        String message = orderService.clearOrdersCache();
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ")
    public Order createOrder(@Valid @RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить заказ по ID")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @Valid @RequestBody Order updatedOrder) {
        return orderService.updateOrder(id, updatedOrder)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ по ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        if (orderService.deleteOrder(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{orderId}/add-product/{productId}")
    @Operation(summary = "Добавить продукт в заказ")
    public ResponseEntity<Order> addProductToOrder(@PathVariable Long orderId,
                                                   @PathVariable Long productId) {
        return orderService.addProductToOrder(orderId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{orderId}/remove-product/{productId}")
    @Operation(summary = "Удалить продукт из заказа")
    public ResponseEntity<Order> removeProductFromOrder(@PathVariable Long orderId,
                                                        @PathVariable Long productId) {
        return orderService.removeProductFromOrder(orderId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}