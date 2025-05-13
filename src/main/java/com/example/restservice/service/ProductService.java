package com.example.restservice.service;

import com.example.restservice.dto.ProductDto;
import com.example.restservice.exception.InvalidProductException;
import com.example.restservice.mapper.ProductMapper;
import com.example.restservice.model.Product;
import com.example.restservice.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    return productRepository.save(product);
                });
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
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


