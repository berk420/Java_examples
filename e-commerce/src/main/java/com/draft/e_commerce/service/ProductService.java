package com.draft.e_commerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.DTO.ProductDTO;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.ProductRepository;
import com.draft.e_commerce.service.interf.ProductServiceInterface;

@Service
public class ProductService implements ProductServiceInterface {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    // DTO'ya dönüştürme metodu
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
    }

    // DTO'dan Product'a dönüştürme metodu
    private Product convertToEntity(ProductDTO productDTO) {
        return new Product(
            productDTO.getName(),
            productDTO.getDescription(),
            productDTO.getPrice(),
            productDTO.getStock()
        );
    }

    @Override
    public ProductDTO getProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        return product != null ? convertToDTO(product) : null;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());

        // `cartEntries` güncellemeyi atla

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
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
