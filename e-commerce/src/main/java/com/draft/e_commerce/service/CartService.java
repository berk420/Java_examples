package com.draft.e_commerce.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartEntry;
import com.draft.e_commerce.model.Customer;
import com.draft.e_commerce.model.DTO.CartDTO;
import com.draft.e_commerce.model.DTO.CartEntryDTO;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
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

    @Transactional
    @Override
    public CartDTO getCart(Long id) {
        Cart cart = cartRepository.findById(id).orElse(null);
        return mapCartToDTO(cart);
    }

    @Transactional
    @Override
    public CartDTO updateCart(CartDTO cart) {
        Cart updatedCart = cartRepository.save(mapDTOToCart(cart));
        return mapCartToDTO(updatedCart);
    }

    @Transactional
    @Override
    public void emptyCart(Long id) {
        cartRepository.findById(id).ifPresent(cart -> {
            cart.getCartEntries().clear();
            cart.setTotalPrice(0);
            cartRepository.save(cart);
        });
    }

    @Transactional
    @Override
    public void addProductToCart(Long cartId, Long productId) {
        cartRepository.findById(cartId).ifPresent(cart -> {
            productRepository.findById(productId).ifPresent(product -> {
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
            });
        });
    }

    @Transactional
    @Override
    public void removeProductFromCart(Long cartId, Long productId) {
        cartRepository.findById(cartId).ifPresent(cart -> {
            CartEntry cartEntry = cart.getCartEntries().stream()
                .filter(entry -> entry.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

            if (cartEntry != null) {
                if (cartEntry.getQuantity() > 1) {
                    cartEntry.setQuantity(cartEntry.getQuantity() - 1);
                    cartEntryRepository.save(cartEntry);
                } else {
                    cart.getCartEntries().remove(cartEntry);
                    cartEntryRepository.delete(cartEntry);
                }
                updateTotalPrice(cart);
            }
        });
    }

    private CartDTO mapCartToDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setCustomerId(cart.getCustomer().getId());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        Set<CartEntryDTO> cartEntryDTOs = cart.getCartEntries().stream()
            .map(entry -> {
                CartEntryDTO dto = new CartEntryDTO();
                dto.setProductId(entry.getProduct().getId());
                dto.setQuantity(entry.getQuantity());
                return dto;
            })
            .collect(Collectors.toSet());

        cartDTO.setCartEntries(cartEntryDTOs);
        return cartDTO;
    }

    private Cart mapDTOToCart(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setId(cartDTO.getId());

        // Assuming that you have a method to find a customer by id
        Customer customer = findCustomerById(cartDTO.getCustomerId());
        cart.setCustomer(customer);

        cart.setTotalPrice(cartDTO.getTotalPrice());

        Set<CartEntry> cartEntries = cartDTO.getCartEntries().stream()
            .map(dto -> {
                CartEntry entry = new CartEntry();
                entry.setProduct(productRepository.findById(dto.getProductId()).orElse(null));
                entry.setQuantity(dto.getQuantity());
                entry.setCart(cart);
                return entry;
            })
            .collect(Collectors.toSet());

        cart.setCartEntries(cartEntries);
        return cart;
    }

    private Customer findCustomerById(Long customerId) {
        // Implement your logic to find a Customer by ID.
        // This could involve calling a CustomerRepository or another service.
        return null; // Replace with actual implementation
    }

    private void updateTotalPrice(Cart cart) {

        int totalPrice = (int) cart.getCartEntries().stream()
        .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
        .sum();
    

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }
}
