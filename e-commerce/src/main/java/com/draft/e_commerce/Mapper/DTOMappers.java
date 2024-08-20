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
import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.DTO.OrderEntryDTO;
import com.draft.e_commerce.model.DTO.ProductDTO;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.CustomerRepository;
import com.draft.e_commerce.service.CartService;
import com.draft.e_commerce.service.ProductService;

@Component
public class DTOMappers {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    //#region Repositories
    @Autowired
    private static CartRepository cartRepository;

    @Autowired
    private static CustomerRepository customerRepository;

    @Autowired
    private static ProductService productService;
    //#endregion

    //#region Methods
    public static CartDTO mapCartToDTO(Cart cart) {
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

    public static Cart mapDTOToCart(CartDTO cartDTO, Long cartId ,Customer customer) {
        try {

            if (cartDTO == null) {
                return null;
            }
    
            Cart cart = new Cart();

            cart.setId(cartDTO.getId());
            cart.setCustomer(customer);

            Set<CartEntry> cartEntries = mapCartEntries(cartDTO, cart);

            cart.setCartEntries(cartEntries);
            long totalPrice = cart
                .getCartEntries()
                .stream()
                .mapToLong(entry -> entry.getProduct().getPrice() * entry.getQuantity())
                .sum();
        
            cart.setTotalPrice((int)totalPrice);
            cartRepository.save(cart);    
            return cart;
        } catch (CustomException e) {
            logger.error("Error setting customer to cart: ", e);
            throw e;
        }
    }

        public static OrderDTO mapOrderToDTO(Order order,long savedCartId) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setCustomerId(order.getCustomer().getId());
        orderDTO.setCartId(savedCartId);
        orderDTO.setTotalPrice(order.getTotalPrice());

        // Düzeltilmiş DTO mapping
        Set<OrderEntryDTO> orderEntryDTOs = order.getOrderEntries().stream()
                .map(entry -> {
                    OrderEntryDTO dto = new OrderEntryDTO();
                    dto.setId(entry.getId());
                    dto.setOrderId(entry.getOrder().getId());
                    dto.setProductId(entry.getProduct().getId()); // Product ID'yi doğru bir şekilde alıyoruz
                    dto.setQuantity(entry.getQuantity());
                    dto.setBasePrice(entry.getBasePrice());
                    return dto;
                })
                .collect(Collectors.toSet());

        orderDTO.setOrderEntries(orderEntryDTOs);

        return orderDTO;
    }

    public static ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getId           (),
            product.getName         (),
            product.getDescription  (),
            product.getPrice        (),
            product.getStock        ()
        );
    }

    public static Product convertToEntity(ProductDTO productDTO) {
        return new Product(
            productDTO.getName          (),
            productDTO.getDescription   (),
            productDTO.getPrice         (),
            productDTO.getStock         ()
        );
    }
    //#endregion

    //#region Functions


private static Set<CartEntry> mapCartEntries(CartDTO cartDTO, Cart cart) {
    return cartDTO.getCartEntries() != null
        ? cartDTO.getCartEntries().stream()
            .map(dto -> {
                CartEntry entry = new CartEntry();

                Long productId = dto.getProductId();

                if (productService == null) {
                    throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,null);
                }
                
                // ProductService'den ürünü al
                Optional<Product> optionalProduct = productService.findById(productId);
                
                // Ürün var mı diye kontrol et
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
