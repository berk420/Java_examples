package com.draft.e_commerce.service.interf;

import com.draft.e_commerce.model.Product;

public interface ProductServiceInterface {
    Product getProduct(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product updatedProduct);
    void deleteProduct(Long id);
    void listBeans();
}