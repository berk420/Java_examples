package com.draft.e_commerce.service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.exception.CustomException;
import com.draft.e_commerce.exception.ErrorCode;
import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.model.DTO.CartDTO;
import com.draft.e_commerce.model.DTO.CartEntryDTO;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.CustomerRepository;
import com.draft.e_commerce.repository.ProductRepository;
import com.draft.e_commerce.service.interf.CartServiceInterface;

@Service
public class CartService implements CartServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartEntryRepository cartEntryRepository;

    @Override
    public CartDTO getCart(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> 
        new CustomException(ErrorCode.CART_NOT_FOUND,null));
        return mapCartToDTO(cart);
    }

    @Override
    public CartDTO createCart(CartDTO cart, Long id) {
        try {
            Cart updatedCart = cartRepository.save(mapDTOToCart(cart, id));
            return mapCartToDTO(updatedCart);
        } catch (Exception e) {
            logger.error("Error creating cart: ", e);
            throw new CustomException(ErrorCode.CART_CREATION_FAILED, e);
        }
    }
    @Override
    public void emptyCart(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> 
            new CustomException(ErrorCode.CART_NOT_FOUND,null));
        cart.getCartEntries().clear();
        cart.setTotalPrice(0);
        cartRepository.save(cart);
    }
    
    @Transactional
    @Override
    public void addProductToCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> 
            new CustomException(ErrorCode.CART_NOT_FOUND,null));
        productRepository.findById(productId).ifPresentOrElse(product -> {
            CartEntry cartEntry = cart.getCartEntries().stream()
                .filter(entry -> entry.getProduct().equals(product))
                .findFirst()
                .orElse(new CartEntry());
    
            cartEntry.setCart(cart);
            cartEntry.setProduct(product);
            cartEntry.setQuantity(cartEntry.getQuantity() + 1);
    
            cart.getCartEntries().add(cartEntry);
            cartEntryRepository.save(cartEntry);
    
            updateTotalPrice(cart);
        }, () -> {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND,null);
        });
    }
    
    @Transactional
    @Override
    public void removeProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> 
            new CustomException(ErrorCode.CART_NOT_FOUND,null));
        CartEntry cartEntry = cart.getCartEntries().stream()
            .filter(entry -> entry.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_IN_CART,null));
    
        if (cartEntry.getQuantity() > 1) {
            cartEntry.setQuantity(cartEntry.getQuantity() - 1);
            cartEntryRepository.save(cartEntry);
        } else {
            cart.getCartEntries().remove(cartEntry);
            cartEntryRepository.delete(cartEntry);
        }
        updateTotalPrice(cart);
    }
    

    private CartDTO mapCartToDTO(Cart cart) {
        if (cart == null) {
            return null;
        }
    
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setCustomerId(cart.getCustomer().getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());
    
        Set<CartEntryDTO> cartEntryDTOs = null;
    
        if (cart.getCartEntries() != null) {
            cartEntryDTOs = cart.getCartEntries().stream()
                .map(entry -> {
                    CartEntryDTO dto = new CartEntryDTO();
                    dto.setProductId(entry.getProduct().getId());
                    dto.setQuantity(entry.getQuantity());
                    return dto;
                })
                .collect(Collectors.toSet());
        }
    
        cartDTO.setCartEntries(cartEntryDTOs);
        return cartDTO;
    }
    

    private Cart mapDTOToCart(CartDTO cartDTO, Long id) {
        if (cartDTO == null) {
            return null;
        }
    
        Cart cart = new Cart();
        cart.setId(cartDTO.getId());
    
        if (cartDTO.getCustomerId() != null) {
            Customer customer = findCustomerById(cartDTO.getCustomerId());
            if (customer != null) {
                cart.setCustomer(customer);
            } else {
                throw new RuntimeException("Customer not found for ID: " + cartDTO.getCustomerId());
            }
        }

        Set<CartEntry> cartEntries = cartDTO.getCartEntries() != null
            ? cartDTO.getCartEntries().stream()
                .map(dto -> {
                    CartEntry entry = new CartEntry();
                    entry.setProduct(productRepository.findById(dto.getProductId()).orElse(null));
                    entry.setQuantity(dto.getQuantity());
                    entry.setCart(cart);
    
                    // Set base price for the CartEntry
                    BigDecimal basePrice = BigDecimal.valueOf(entry.getProduct().getPrice()); // Assuming product price is a long
                    entry.setBasePrice(basePrice);
    
                    return entry;
                })
                .collect(Collectors.toSet())
            : null;

        cart.setCartEntries(cartEntries);

        
        Integer totalPrice = (int) cart.getCartEntries().stream()
        .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
        .sum();

        cart.setTotalPrice(totalPrice);
        return cart;
    }
    
    
    

    @Autowired
    private CustomerRepository customerRepository;

    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }


    private void updateTotalPrice(Cart cart) {
        int totalPrice = (int) cart.getCartEntries().stream()
            .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
            .sum();

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}
