package com.example.restservice.service;

import com.example.restservice.dto.ProductDto;
import com.example.restservice.exception.InvalidProductException;
import com.example.restservice.exception.ProductInOrderException;
import com.example.restservice.mapper.ProductMapper;
import com.example.restservice.model.Order;
import com.example.restservice.model.Product;
import com.example.restservice.repository.OrderRepository;
import com.example.restservice.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final OrderRepository orderRepository;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.orderRepository = orderRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        if ("Test".equalsIgnoreCase(product.getName())) {
            throw new InvalidProductException("Нельзя создавать товар с именем 'Test'");
        }
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setPrice(updatedProduct.getPrice());

                    Product saved = productRepository.save(existing);

                    // 💥 Перерасчёт всех заказов, где используется обновлённый продукт
                    List<Order> affectedOrders = orderRepository
                            .findOrdersByProductName(saved.getName());

                    for (Order order : affectedOrders) {
                        order.recalculateTotalAmount();
                    }

                    orderRepository.saveAll(affectedOrders);

                    return saved;
                });
    }


    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }

        Product product = productRepository.findById(id).orElseThrow();

        boolean inUse = !orderRepository.findOrdersByProductName(product.getName()).isEmpty();
        if (inUse) {
            throw new ProductInOrderException("Товар используется в "
                    + "заказах и не может быть удалён.");
        }

        productRepository.deleteById(id);
        return true;
    }


    @Transactional
    public List<ProductDto> createProducts(List<ProductDto> productDtos) {
        List<Product> newProducts = productDtos.stream()
                .map(productMapper::toEntity)
                .toList();

        productRepository.saveAll(newProducts);

        return newProducts.stream()
                .map(productMapper::toDto)
                .toList();
    }
}


