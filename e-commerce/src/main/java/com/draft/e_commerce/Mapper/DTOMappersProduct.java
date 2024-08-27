package com.draft.e_commerce.Mapper;

import org.springframework.stereotype.Component;

import com.draft.e_commerce.model.DTO.ProductDTO;
import com.draft.e_commerce.model.Product;

@Component
public class DTOMappersProduct {

    public ProductDTO convertToDTO(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
    
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
    }
    
    public Product convertToEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("ProductDTO cannot be null");
        }
    
        return new Product(
            productDTO.getName(),
            productDTO.getDescription(),
            productDTO.getPrice(),
            productDTO.getStock()
        );
    }
    
}
