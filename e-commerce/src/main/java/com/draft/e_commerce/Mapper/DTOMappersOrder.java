package com.draft.e_commerce.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.draft.e_commerce.model.DTO.OrderDTO;
import com.draft.e_commerce.model.DTO.OrderEntryDTO;
import com.draft.e_commerce.model.Order;


@Component
public class DTOMappersOrder {

    
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

    

    public OrderDTO convertToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderCode(order.getOrderCode());
        orderDTO.setCustomerId(order.getCustomer().getId());
        orderDTO.setCartId(null);  // ya da uygun bir değer
        orderDTO.setTotalPrice(order.getTotalPrice());
    
        Set<OrderEntryDTO> orderEntryDTOs = order.getOrderEntries().stream().map(entry -> {
            OrderEntryDTO dto = new OrderEntryDTO();
            dto.setId(entry.getId());
            dto.setOrderId(entry.getOrder().getId());
            dto.setProductId(entry.getProduct().getId());
            dto.setQuantity(entry.getQuantity());
            dto.setBasePrice(entry.getBasePrice());
            return dto;
        }).collect(Collectors.toSet());
    
        orderDTO.setOrderEntries(orderEntryDTOs);
    
        return orderDTO;
    }
    
}
