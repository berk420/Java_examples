package com.draft.e_commerce.service;


import java.math.BigDecimal;
import java.util.Optional;
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
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartEntryRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.service.interf.CartServiceInterface;
@Service
public class CartService implements CartServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    //#region Dependencies

    @Autowired
    private CartRepository          cartRepository;

    @Autowired
    private CustomerService         customerService;

    @Autowired
    private CartEntryRepository     cartEntryRepository;

    @Autowired
    private ProductService          productService; 



    //#endregion

    //#region Methods
    @Override
    public CartDTO getCart(Long cartId,Long customerId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> 
        new CustomException(ErrorCode.CART_NOT_FOUND, null));

        customerService.findById(customerId).orElseThrow(() -> 
        new CustomException(ErrorCode.CART_NOT_FOUND, null));

        return mapCartToDTO(cart);
    }

    @Override
    public CartDTO createCart(CartDTO cartDTO,Long customerId) {

        try {
            cartRepository.findByCustomerId(customerId).ifPresent(cart -> {
                throw new CustomException(ErrorCode.CART_ALREADY_EXISTS, null);
            });
        
        } catch (Exception e) {
            logger.error("Error creating cart: ", e);  
            throw new CustomException(ErrorCode.CART_ALREADY_EXISTS, e);
        }

        try {
            Customer custom = customerService.findById(customerId).orElseThrow(() ->
                new CustomException(ErrorCode.CUSTOMER_NOT_FOUND, null));
    
            Cart updatedCart = cartRepository.save(mapDTOToCart(cartDTO, custom));
    
            return mapCartToDTO(updatedCart);
        } catch (Exception e) {
            logger.error("Error creating cart: ", e);  
            throw new CustomException(ErrorCode.CART_CREATION_FAILED, e);
        }
    }
    
    

    public CartDTO mapCartToDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDTO cartDTO = new CartDTO();

        cartDTO.setId           (cart.getId());
        cartDTO.setCustomerId   (cart.getCustomer().getId());
        cartDTO.setTotalPrice   (cart.getTotalPrice());

        java.util.Set<CartEntryDTO> cartEntryDTOs = null;

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


    @Override
    public CartDTO emptyCart(Long cartId,Long customerId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> 
            new CustomException(ErrorCode.CART_NOT_FOUND, null));

        customerService.findById(customerId).orElseThrow(() -> 
            new CustomException(ErrorCode.CART_NOT_FOUND, null));
    

        cart.getCartEntries().clear();
        cart.setTotalPrice(0);

        Cart updatedCart = cartRepository.save(cart);

        return mapCartToDTO(updatedCart);
    }

    @Override
    public CartDTO addProductToCart(Long cartId, Long productId,Long customerId) {
        return updateCartWithProduct(cartId, productId, customerId,true);
    }

    @Override
    public CartDTO removeProductFromCart(Long cartId, Long productId,Long customerId) {
        return updateCartWithProduct(cartId, productId, customerId,false);
    }
    //#endregion

    //#region Functions
    @Transactional
    private CartDTO updateCartWithProduct(Long cartId, Long productId, Long customerId, boolean isAdding) {
        Cart cart = cartRepository
            .findById(cartId)
            .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND, null));
    
        customerService.findById(customerId)
            .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND, null));
    
        productService.findById(productId).ifPresentOrElse(product -> {
            CartEntry cartEntry = cart.getCartEntries().stream()
                .filter(entry -> entry.getProduct().getId().equals(product.getId())) // ID bazlı karşılaştırma
                .findFirst()
                .orElseGet(() -> { 
                    // Eğer CartEntry bulunamazsa yeni bir CartEntry oluşturur
                    CartEntry newEntry = new CartEntry();
                    newEntry.setCart(cart);
                    newEntry.setProduct(product);
                    newEntry.setQuantity(0); // İlk başta quantity 0 olarak ayarlanıyor, sonra artırılacak
                    newEntry.setBasePrice(BigDecimal.ZERO); // İlk başta basePrice 0 olarak ayarlanıyor, sonra artırılacak
                    cart.getCartEntries().add(newEntry);
                    return newEntry;
                });
    
            if (isAdding) {
                cartEntry.setQuantity(cartEntry.getQuantity() + 1);
                cartEntry.setBasePrice(cartEntry.getBasePrice().add(BigDecimal.valueOf(product.getPrice())));
            } else {
                if (cartEntry.getQuantity() > 1) {
                    cartEntry.setQuantity(cartEntry.getQuantity() - 1);
                } else {
                    cart.getCartEntries().remove(cartEntry);
                    cartEntryRepository.delete(cartEntry);
                }
            }
    
            cartEntryRepository.save(cartEntry);
            updateTotalPrice(cart);
        }, () -> {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null);
        });
    
        return mapCartToDTO(cart);
    }
    

    

    private void updateTotalPrice(Cart cart) {
        int totalPrice = (int) cart
            .getCartEntries()
            .stream()
            .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
            .sum();

        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    public Cart findById(Long cartId) {
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND, null));
    }
    
    public void deleteById(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    public Long getCartIdByCustomerId(Long customerId) {
        return cartRepository.findCartIdByCustomerId(customerId);
    }
    
    //#endregion

    //#region ayır bunları başka yere(funklara ayır)
    
        public Long findCartIdByCustomerId(Long customerId) {
            return cartRepository.findCartIdByCustomerId(customerId);
        }

        
        public Cart save(Cart cart) {
            return cartRepository.save(cart);
        }

    //#endregion

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
