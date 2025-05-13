package com.example.restservice.service;

import com.example.restservice.cache.OrderCache;
import com.example.restservice.exception.InvalidOrderException;
import com.example.restservice.model.Order;
import com.example.restservice.model.Product;
import com.example.restservice.repository.OrderRepository;
import com.example.restservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderCache orderCache;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1000.00"));

        order = new Order();
        order.setId(1L);
        order.setCustomerName("Alice");
        order.setProducts(new HashSet<>(List.of(product)));
        order.recalculateTotalAmount();
    }

    @Test
    void testFindOrdersByProductName_fromCache() {
        when(orderCache.contains("Laptop")).thenReturn(true);
        when(orderCache.get("Laptop")).thenReturn(List.of(order));

        List<Order> result = orderService.findOrdersByProductName("Laptop");

        assertThat(result).hasSize(1);
        verify(orderCache, never()).put(any(), any());
    }

    @Test
    void testFindOrdersByProductName_fromDatabase() {
        when(orderCache.contains("Laptop")).thenReturn(false);
        when(orderRepository.findOrdersByProductName("Laptop")).thenReturn(List.of(order));

        List<Order> result = orderService.findOrdersByProductName("Laptop");

        assertThat(result).hasSize(1);
        verify(orderCache).put("Laptop", List.of(order));
    }

    @Test
    void testClearOrdersCache() {
        String result = orderService.clearOrdersCache();
        verify(orderCache).clear();
        assertThat(result).contains("Очистка кэша");
    }

    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        List<Order> result = orderService.getAllOrders();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetOrderById_found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThat(orderService.getOrderById(1L)).isPresent();
    }

    @Test
    void testGetOrderById_notFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(orderService.getOrderById(99L)).isEmpty();
    }

    @Test
    void testCreateOrder_valid() {
        when(productRepository.findAllById(Set.of(1L))).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);
        assertThat(result.getCustomerName()).isEqualTo("Alice");
        verify(orderCache).invalidate("Laptop");
    }

    @Test
    void testCreateOrder_invalidProduct_shouldThrow() {
        when(productRepository.findAllById(Set.of(1L))).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.createOrder(order))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessageContaining("не найдены");
    }

    @Test
    void testUpdateOrder_found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order updated = new Order();
        updated.setCustomerName("Bob");
        updated.setProducts(new HashSet<>(List.of(product)));

        Optional<Order> result = orderService.updateOrder(1L, updated);
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerName()).isEqualTo("Bob");
    }

    @Test
    void testDeleteOrder_exists() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        boolean result = orderService.deleteOrder(1L);
        assertThat(result).isTrue();
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void testDeleteOrder_notFound() {
        when(orderRepository.existsById(2L)).thenReturn(false);
        boolean result = orderService.deleteOrder(2L);
        assertThat(result).isFalse();
    }

    @Test
    void testAddProductToOrder_valid() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Optional<Order> result = orderService.addProductToOrder(1L, 1L);
        assertThat(result).isPresent();
        verify(orderCache).invalidate("Laptop");
    }

    @Test
    void testAddProductToOrder_productNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Order> result = orderService.addProductToOrder(1L, 1L);
        assertThat(result).isEmpty();
    }

    @Test
    void testRemoveProductFromOrder_valid() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Optional<Order> result = orderService.removeProductFromOrder(1L, 1L);
        assertThat(result).isPresent();
        verify(orderCache).invalidate("Laptop");
    }
}

