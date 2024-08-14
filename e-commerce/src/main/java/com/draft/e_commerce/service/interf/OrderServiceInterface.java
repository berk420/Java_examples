
package com.draft.e_commerce.service.interf;

import com.draft.e_commerce.model.Order;

public interface OrderServiceInterface
{
    Order placeOrder(Long cartId);
    Order getOrderById(Long id);
}
