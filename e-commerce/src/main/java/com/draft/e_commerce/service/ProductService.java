package com.draft.e_commerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.ProductRepository;
import com.draft.e_commerce.service.interf.ProductServiceInterface;

@Service
public class ProductService implements ProductServiceInterface{

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProduct(id);
        if (existingProduct == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
    
        // Mevcut ürünü güncelle
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
    
        // `cartEntries` alanını güncellemeyi atla
        // existingProduct.setCartEntries(updatedProduct.getCartEntries()); satırını kaldırın veya şartlı hale getirin.
    
        // Güncellenmiş ürünü kaydet ve geri dön
        return productRepository.save(existingProduct);
    }
    
    

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void listBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        logger.info("---------------------------------------------------");
        logger.info("Beans in ApplicationContext:");
        for (String beanName : beanNames) {
            logger.info(beanName);
        }
        logger.info("---------------------------------------------------");
    }
}
