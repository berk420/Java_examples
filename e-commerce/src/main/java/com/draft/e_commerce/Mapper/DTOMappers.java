package com.draft.e_commerce.Mapper;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.DTO.CartDTO;
import com.draft.e_commerce.model.DTO.CartEntryDTO;
import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.DTO.OrderEntryDTO;
import com.draft.e_commerce.model.DTO.ProductDTO;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.Product;

@Component
public class DTOMappers {

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

        public  OrderDTO mapOrderToDTO(Order order,long savedCartId) {
        if (order == null) {
            return null;
        }

        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setId              (order.getId());
        orderDTO.setOrderCode       (order.getOrderCode());
        orderDTO.setCustomerId      (order.getCustomer().getId());
        orderDTO.setCartId          (savedCartId);
        orderDTO.setTotalPrice      (order.getTotalPrice());

        Set<OrderEntryDTO> orderEntryDTOs = order
                .getOrderEntries()  
                .stream()
                .map(entry -> {
                    OrderEntryDTO dto = new OrderEntryDTO();
                    dto.setId           (entry.getId());
                    dto.setOrderId      (entry.getOrder().getId());
                    dto.setProductId    (entry.getProduct().getId());
                    dto.setQuantity     (entry.getQuantity());
                    dto.setBasePrice    (entry.getBasePrice());
                    return dto;
                })
                .collect(Collectors.toSet());

        orderDTO.setOrderEntries(orderEntryDTOs);

        return orderDTO;
    }

    public ProductDTO convertToDTO(Product product) {

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
    
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock()
        );
    }
    
    public Product convertToEntity(ProductDTO productDTO) {
        
        if (productDTO == null) {
            throw new IllegalArgumentException("ProductDTO cannot be null");
        }
    
        return new Product(
            productDTO.getName(),
            productDTO.getDescription(),
            productDTO.getPrice(),
            productDTO.getStock()
        );
    }
    

}
