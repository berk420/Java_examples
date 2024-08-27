package com.draft.e_commerce.Mapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.model.DTO.CartDTO;
import com.draft.e_commerce.model.DTO.CartEntryDTO;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.service.CartService;
import com.draft.e_commerce.service.ProductService;


@Component
public class DTOMappersCart {
    

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private ProductService productService; 



    public CartDTO mapCartToDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO cartDTO = new CartDTO();

        cartDTO.setId           (cart.getId());
        cartDTO.setCustomerId   (cart.getCustomer().getId());
        cartDTO.setTotalPrice   (cart.getTotalPrice());

        Set<CartEntryDTO> cartEntryDTOs = null;

        if (cart.getCartEntries() != null) {
            cartEntryDTOs = cart.getCartEntries().stream()
                .map(entry -> {
                    CartEntryDTO dto = new CartEntryDTO();
                    dto.setProductId    (entry.getProduct().getId());
                    dto.setQuantity     (entry.getQuantity());
                    return dto;
                })
                .collect(Collectors.toSet());
        }

        cartDTO.setCartEntries(cartEntryDTOs);

        return cartDTO;
    }

    public Cart mapDTOToCart(CartDTO cartDTO,Customer customer) {
        try {

            if (cartDTO == null) {
                return null;
            }

            Cart cart = new Cart();

            cart.setId(cartDTO.getId());
            cart.setCustomer(customer);
            cart.setCartEntries(mapCartEntries(cartDTO, cart));

            long totalPrice = cart
                .getCartEntries()
                .stream()
                .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
                .sum();
        
            cart.setTotalPrice((int)totalPrice);
            return cart;
        } catch (CustomException e) {
            logger.error("Error setting customer to cart: ", e);
            throw e;
        }
    }

    private  java.util.Set<CartEntry> mapCartEntries(CartDTO cartDTO, Cart cart) {
        
    return cartDTO.getCartEntries() != null
        ? cartDTO.getCartEntries().stream()
            .map(dto -> {
                CartEntry entry = new CartEntry();

                Long productId = dto.getProductId();

                Optional<Product> optionalProduct = productService.findById(productId);
                
                Product product = optionalProduct.orElseThrow(() -> 
                    new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));
                
                // Set the product
                entry.setProduct(product);
                entry.setQuantity(dto.getQuantity());
                entry.setCart(cart);

                long basePrice = entry.getProduct().getPrice(); // Assuming product price is a long
                entry.setBasePrice(BigDecimal.valueOf(basePrice));

                return entry;
            })
            .collect(Collectors.toSet())
        : null;
    }


}
