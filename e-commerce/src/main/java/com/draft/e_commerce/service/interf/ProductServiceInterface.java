package com.draft.e_commerce.service.interf;

import com.draft.e_commerce.model.DTO.ProductDTO;

public interface ProductServiceInterface {
    ProductDTO getProduct(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO updatedProduct);
    void deleteProduct(Long id);
    void listBeans();
}