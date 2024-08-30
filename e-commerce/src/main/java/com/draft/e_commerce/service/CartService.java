package com.draft.e_commerce.service;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.draft.e_commerce.Mapper.DTOMappersCart;
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

    
    @Autowired
    private DTOMappersCart     dTOMappersCart; 




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
    
            Cart updatedCart = cartRepository.save(dTOMappersCart.mapDTOToCart(cartDTO, custom));
    
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


    @Transactional
    @Override
    public CartDTO emptyCart(Long cartId, Long customerId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND, null));
    
        customerService.findById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND, null));
    
        cart.getCartEntries().clear();
    
        cartEntryRepository.deleteByCartId(cartId);
    
        cart.setTotalPrice(0);
    
        Cart updatedCart = cartRepository.save(cart);
    
        return mapCartToDTO(updatedCart);
    }
    
    @Override
    public CartDTO addProductToCart(Long cartId, Long productId, Long customerId, int count) {

        if (cartRepository.existsById(cartId)) {
            return updateCartWithProduct(cartId, productId, customerId, count, true);
        } else {
            return createCartWithProduct(productId, customerId, count);
        }
    }
    
    @Override
    public CartDTO removeProductFromCart(Long cartId, Long productId, Long customerId, int count) {
        return updateCartWithProduct(cartId, productId, customerId, count, false);
    }
    //#endregion

    //#region Functions
    @Transactional
    private CartDTO updateCartWithProduct(Long cartId, Long productId, Long customerId, int count, boolean isAdding) {
        // Müşteri doğrulaması
        customerService.findById(customerId)
            .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND, null));

        // Sepetin müşteriye ait olup olmadığını kontrol et
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND, null));

        if (!cart.getCustomer().getId().equals(customerId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS, null);
        }

        // Ürünü bul
        Product product = productService.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));

        // Sepet girişini kontrol et ve ekleme/çıkarma işlemlerini yap
        CartEntry cartEntry = cart.getCartEntries().stream()
            .filter(entry -> entry.getProduct().getId().equals(product.getId()))
            .findFirst()
            .orElse(null);

        if (isAdding) {
            if (cartEntry == null) {
                cartEntry = new CartEntry();
                cartEntry.setCart(cart);
                cartEntry.setProduct(product);
                cartEntry.setQuantity(count);
                cartEntry.setBasePrice(BigDecimal.valueOf(product.getPrice() * count));
                cart.getCartEntries().add(cartEntry);
            } else {
                cartEntry.setQuantity(cartEntry.getQuantity() + count);
                cartEntry.setBasePrice(cartEntry.getBasePrice().add(BigDecimal.valueOf(product.getPrice() * count)));
            }
            cartEntryRepository.save(cartEntry);
        } else {
            if (cartEntry != null) {
                int newQuantity = cartEntry.getQuantity() - count;
                if (newQuantity > 0) {
                    cartEntry.setQuantity(newQuantity);
                    cartEntry.setBasePrice(cartEntry.getBasePrice().subtract(BigDecimal.valueOf(product.getPrice() * count)));
                    cartEntryRepository.save(cartEntry);
                } else {
                    cart.getCartEntries().remove(cartEntry); // Cart'tan siliniyor
                    cartEntryRepository.deleteById(cartEntry.getId()); // Veri tabanından siliniyor
                    logger.info("Product with ID " + productId + " removed from cart.");
                }
            } else {
                logger.error("Product with ID " + productId + " is not in the cart.");
            }
        }

        updateTotalPrice(cart);
        cartRepository.save(cart);

        return mapCartToDTO(cart);
    }

    private CartDTO createCartWithProduct(Long productId, Long customerId, int count) {
        // Müşteriyi bul
        Customer customer = customerService.findById(customerId)
            .orElseThrow(() -> new CustomException(ErrorCode.CUSTOMER_NOT_FOUND, null));

        // Ürünü bul
        Product product = productService.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, null));

        // Yeni CartDTO oluştur ve bilgileri set et
        CartDTO newCartDTO = new CartDTO();
        newCartDTO.setCustomerId(customerId);
        
        // Yeni bir CartEntryDTO oluştur ve bilgileri set et
        CartEntryDTO cartEntryDTO = new CartEntryDTO();
        cartEntryDTO.setProductId(productId);
        cartEntryDTO.setQuantity(count);

        // CartEntryDTO'yu CartDTO'ya ekle
        Set<CartEntryDTO> cartEntryDTOSet = new HashSet<>();
        cartEntryDTOSet.add(cartEntryDTO);
        newCartDTO.setCartEntries(cartEntryDTOSet);

        // Toplam fiyatı hesapla ve set et
        newCartDTO.setTotalPrice((int) (product.getPrice() * count));

        // Yeni Cart'ı oluştur ve CartDTO olarak döndür
        CartDTO cartDTO = createCart(newCartDTO, customerId);
        return addProductToCart(cartDTO.getId(), productId, customerId, count);
    }

    private void updateTotalPrice(Cart cart) {

        if (cart.getCartEntries() == null || cart.getCartEntries().isEmpty()) {
            throw new CustomException(ErrorCode.CART_ENTRY_DELETED, null);
        }
    
        int totalPrice = (int) cart.getCartEntries()
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
    
    public Long findCartIdByCustomerId(Long customerId) {
        return cartRepository.findCartIdByCustomerId(customerId);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    //#endregion








}
