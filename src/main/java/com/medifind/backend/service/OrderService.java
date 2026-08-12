package com.medifind.backend.service;

import com.medifind.backend.dto.request.PlaceOrderRequest;
import com.medifind.backend.dto.response.OrderItemResponse;
import com.medifind.backend.dto.response.OrderResponse;
import com.medifind.backend.entity.*;
import com.medifind.backend.enums.OrderStatus;
import com.medifind.backend.enums.PaymentMethod;
import com.medifind.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PharmacyInventoryRepository inventoryRepository;

    public OrderService(
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PharmacyInventoryRepository inventoryRepository
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public OrderResponse placeOrder(
            String customerEmail,
            PlaceOrderRequest request
    ) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart is empty")
                );

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            PharmacyInventory inventory = cartItem.getInventory();

            if (!inventory.isActive()) {
                throw new IllegalArgumentException(
                        inventory.getMedicine().getMedicineName()
                                + " is unavailable"
                );
            }

            if (cartItem.getQuantity() > inventory.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for "
                                + inventory.getMedicine().getMedicineName()
                );
            }

            BigDecimal subtotal =
                    inventory.getSellingPrice().multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    );

            totalAmount = totalAmount.add(subtotal);
        }

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setPharmacy(cart.getPharmacy());
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        order.setDeliveryAddress(request.getDeliveryAddress().trim());
        order.setPlacedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            PharmacyInventory inventory = cartItem.getInventory();

            BigDecimal subtotal =
                    inventory.getSellingPrice().multiply(
                            BigDecimal.valueOf(cartItem.getQuantity())
                    );

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setInventory(inventory);
            orderItem.setMedicineName(
                    inventory.getMedicine().getMedicineName()
            );
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(inventory.getSellingPrice());
            orderItem.setSubtotal(subtotal);

            orderItemRepository.save(orderItem);

            inventory.setQuantity(
                    inventory.getQuantity() - cartItem.getQuantity()
            );

            inventoryRepository.save(inventory);
        }

        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders(String customerEmail) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );

        return orderRepository
                .findByCustomerIdOrderByPlacedAtDesc(customer.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderById(
            String customerEmail,
            Long orderId
    ) {

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found")
                );

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException(
                    "This order does not belong to the customer"
            );
        }

        return mapToResponse(order);
    }

    private String generateOrderNumber() {

        return "MF-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items =
                orderItemRepository.findByOrderId(order.getId())
                        .stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getInventory().getId(),
                                item.getMedicineName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getSubtotal()
                        ))
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getPharmacy().getId(),
                order.getPharmacy().getPharmacyName(),
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getPaymentMethod(),
                order.getDeliveryAddress(),
                order.getPlacedAt(),
                items
        );
    }
}