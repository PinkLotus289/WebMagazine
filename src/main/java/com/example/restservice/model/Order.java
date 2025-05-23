package com.example.restservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя клиента не может быть пустым")
    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @NotNull(message = "Дата заказа обязательна")
    @PastOrPresent(message = "Дата заказа не может быть в будущем")
    @Column(nullable = false)
    private LocalDateTime orderDate;

    @ManyToMany
    @JoinTable(
            name = "order_product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )

    @NotNull(message = "Список продуктов не может быть null")
    @Size(min = 1, message = "Нужно указать хотя бы один продукт")
    private Set<Product> products = new HashSet<>();

    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(String customerName, Set<Product> products) {
        this.customerName = customerName;
        this.products = products != null ? products : new HashSet<>();
        this.orderDate = LocalDateTime.now();
        recalculateTotalAmount();
    }


    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setProducts(Set<Product> products) {
        this.products = products != null ? products : new HashSet<>();
        recalculateTotalAmount();
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    public void recalculateTotalAmount() {
        this.totalAmount = products.stream()
                .map(Product::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

