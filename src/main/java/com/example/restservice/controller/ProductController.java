package com.example.restservice.controller;

import com.example.restservice.dto.ProductDto;
import com.example.restservice.dto.ProductListDto;
import com.example.restservice.model.Product;
import com.example.restservice.service.ProductService;
import com.example.restservice.service.VisitCounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/products")
@Validated
@Tag(name = "Товары", description = "Операции с продуктами")
public class ProductController {

    private final ProductService productService;
    private final VisitCounterService counterService;

    public ProductController(ProductService productService, VisitCounterService counterService) {
        this.productService = productService;
        this.counterService = counterService;
    }

    @GetMapping
    @Operation(summary = "Получить список всех товаров")
    public List<Product> getAllProducts() {
        counterService.increment();
        return productService.getAllProducts();
    }

    @GetMapping("/counter")
    @Operation(summary = "Получить количество посещений /products")
    public Map<String, Integer> getVisitCount() {
        return Map.of("count", counterService.getValue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый товар")
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар по ID")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @Valid @RequestBody Product updatedProduct) {
        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар по ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Создание нескольких товаров (bulk)",
            description = "Создает несколько товаров и возвращает их данные")
    @PostMapping("/bulk")
    public ResponseEntity<List<ProductDto>> createProductsBulk(
            @Parameter(description = "Обёртка с продуктами")
            @Valid @RequestBody ProductListDto productListDto) {

        List<ProductDto> createdProducts = productService.createProducts(
                productListDto.getProducts());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProducts);
    }

}



