
package com.draft.e_commerce.service.interf;

import com.draft.e_commerce.model.DTO.OrderDTO;

public interface OrderServiceInterface
{
    OrderDTO placeOrder(Long cartId);
    OrderDTO getOrderById(Long orderId,Long custoemrId);
}
