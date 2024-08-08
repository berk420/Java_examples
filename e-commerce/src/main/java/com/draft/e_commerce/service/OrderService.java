package com.draft.e_commerce.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.draft.e_commerce.model.Cart;
import com.draft.e_commerce.model.CartProduct;
import com.draft.e_commerce.model.Order;
import com.draft.e_commerce.model.Product;
import com.draft.e_commerce.repository.CartProductRepository;
import com.draft.e_commerce.repository.CartRepository;
import com.draft.e_commerce.repository.OrderRepository;
import com.draft.e_commerce.repository.ProductRepository;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartProductRepository cartProductRepository;



    public Order placeOrder(Long cartId) {
        try {
            // Cart'ı bul
            Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new IllegalArgumentException("Cart not found"));
            if (cart.isOrderState()) {
                throw new IllegalStateException("This cart is already associated with an order.");
            }

            //Liste gerek yok sadece 1 tane olacak zaten

            // CartProduct'ları bul
            Optional<CartProduct> cartProductOptional = cartProductRepository.findByIdCartId(cartId);
            Optional<Cart> cartOptional = cartRepository.findById(cartId);


            if (cartProductOptional.isPresent() && cartOptional.isPresent()) {

                Set<Product> products = cart.getProducts();
                CartProduct cartProduct;

                for (Product product : products) {
                    System.out.println(product); // veya ürün bilgilerini detaylı bir şekilde yazdırabilirsiniz
                    // Örneğin, ürünün adını ve fiyatını yazdırmak:
                    System.out.println("Product Name: " + product.getName());
                    System.out.println("Product Price: " + product.getPrice());
                    cartProduct = cartProductOptional.get();

                    Integer quantity = cartProduct.getQuantity();

                    // Product stoklarını güncelle
                    product.setStock(product.getStock() - quantity); // Assuming Product class has a setStock method
                    // Product'ı kaydet
                    productRepository.save(product); // Eğer bir ProductRepository'niz varsa
                }
                

            }

            // Order oluştur
            Order order = new Order();
            order.setOrderCode(generateOrderCode());
            order.setCart(cart);
            order.setCustomer(cart.getCustomer());

            long totalPrice = calculateTotalPrice(cart);
            order.setTotalPrice(totalPrice);

            cart.setOrderState(true);
            cartRepository.save(cart);


            // Cart'ı ve CartProduct'ları sil
            //cartProductRepository.deleteByCartId(cartId); // Tüm cartProduct'ları silmek için
            //cartRepository.deleteById(cartId); // Cart'ı silmek için

            return orderRepository.save(order);
        } catch (IllegalStateException e) {
            logger.error("Error placing order: ", e);
            throw e;
        }
    }

    private long calculateTotalPrice(Cart cart) {
        return cart.getProducts()
                   .stream()
                   .mapToLong(product -> product.getPrice())
                   .sum();
    }

    private String generateOrderCode() {
        return UUID.randomUUID().toString();
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            System.out.println("Order not found for id: " + id);
        } else {
            System.out.println("Order found: " + order);
        }
        return order;
    }
    
}
