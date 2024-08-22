package com.draft.e_commerce.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.Mapper.DTOMappers;
import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.DTO.ProductDTO;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.ProductRepository;
import com.draft.e_commerce.service.interf.ProductServiceInterface;

@Service
public class ProductService implements ProductServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    //#region Dependencies
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DTOMappers DTOMappers;
    //#endregion

    //#region Methods

    @Override
    public ProductDTO getProduct(Long id) {

        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));
                
        //return DTOMappers.convertToDTO(product);
        return DTOMappers.convertToDTO(product);


    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {

        Product product =       DTOMappers.convertToEntity(productDTO);
        Product savedProduct =  productRepository.save(product);

        return DTOMappers.convertToDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));

        if (productDTO.getStock() < 0) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_DATA, null);
        }

        existingProduct.setName         (productDTO.getName());
        existingProduct.setDescription  (productDTO.getDescription());
        existingProduct.setPrice        (productDTO.getPrice());
        existingProduct.setStock        (productDTO.getStock());

        Product updatedProduct = productRepository.save(existingProduct);
        return DTOMappers.convertToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.  findById      (id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));
        productRepository.  deleteById    (id);
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

    // Yeni findById metodu
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    public Product save(Product product) {
        return productRepository.save(product);
    }

    //#endregion

    //#region Functions
    public  ProductDTO convertToDTO(Product product) {

        productRepository.  findById      (product.getId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));

        return new ProductDTO(
            product.getId           (),
            product.getName         (),
            product.getDescription  (),
            product.getPrice        (),
            product.getStock        ()
        );
    }

    public  Product convertToEntity(ProductDTO productDTO) {

        productRepository.  findById      (productDTO.getId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));

        return new Product(
            productDTO.getName          (),
            productDTO.getDescription   (),
            productDTO.getPrice         (),
            productDTO.getStock         ()
        );
    }
    //#endregion
}
