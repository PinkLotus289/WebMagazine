package com.example.restservice.mapper;

import com.example.restservice.dto.ProductDto;
import com.example.restservice.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);
}
