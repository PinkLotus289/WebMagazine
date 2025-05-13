package com.example.restservice.service;

import com.example.restservice.dto.ProductDto;
import com.example.restservice.exception.InvalidProductException;
import com.example.restservice.mapper.ProductMapper;
import com.example.restservice.model.Product;
import com.example.restservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("999.99"));

        productDto = new ProductDto();
        productDto.setName("Laptop");
        productDto.setPrice(new BigDecimal("999.99"));
    }

    @Test
    void testGetAllProducts_shouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        List<Product> result = productService.getAllProducts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void testGetProductById_found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> result = productService.getProductById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Laptop");
    }

    @Test
    void testGetProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.getProductById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void testCreateProduct_valid() {
        when(productRepository.save(product)).thenReturn(product);
        Product saved = productService.createProduct(product);
        assertThat(saved.getName()).isEqualTo("Laptop");
    }

    @Test
    void testCreateProduct_invalidName_shouldThrow() {
        product.setName("Test");
        assertThatThrownBy(() -> productService.createProduct(product))
                .isInstanceOf(InvalidProductException.class)
                .hasMessageContaining("Нельзя создавать товар с именем 'Test'");
    }

    @Test
    void testUpdateProduct_found() {
        Product updated = new Product();
        updated.setName("Monitor");
        updated.setPrice(new BigDecimal("199.99"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Optional<Product> result = productService.updateProduct(1L, updated);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Monitor");
    }

    @Test
    void testUpdateProduct_notFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Product> result = productService.updateProduct(999L, product);
        assertThat(result).isEmpty();
    }

    @Test
    void testDeleteProduct_found() {
        when(productRepository.existsById(1L)).thenReturn(true);
        boolean result = productService.deleteProduct(1L);
        verify(productRepository).deleteById(1L);
        assertThat(result).isTrue();
    }

    @Test
    void testDeleteProduct_notFound() {
        when(productRepository.existsById(999L)).thenReturn(false);
        boolean result = productService.deleteProduct(999L);
        assertThat(result).isFalse();
    }

    @Test
    void testCreateProducts_bulkInsert() {
        when(productMapper.toEntity(productDto)).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productDto);
        when(productRepository.saveAll(anyList())).thenReturn(List.of(product));

        List<ProductDto> result = productService.createProducts(List.of(productDto));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
    }
}
