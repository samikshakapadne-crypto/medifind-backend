package com.medifind.backend.service;

import com.medifind.backend.dto.request.AddCartItemRequest;
import com.medifind.backend.dto.request.UpdateCartItemRequest;
import com.medifind.backend.dto.response.CartItemResponse;
import com.medifind.backend.dto.response.CartResponse;
import com.medifind.backend.entity.*;
import com.medifind.backend.enums.PharmacyApprovalStatus;
import com.medifind.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PharmacyInventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            PharmacyInventoryRepository inventoryRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse addItem(
            String customerEmail,
            AddCartItemRequest request
    ) {

        User customer = getCustomer(customerEmail);

        PharmacyInventory inventory = inventoryRepository
                .findById(request.getInventoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Inventory not found")
                );

        validateInventory(inventory, request.getQuantity());

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    newCart.setPharmacy(inventory.getPharmacy());
                    return cartRepository.save(newCart);
                });

        if (cart.getPharmacy() != null
                && !cart.getPharmacy().getId()
                .equals(inventory.getPharmacy().getId())) {

            throw new IllegalArgumentException(
                    "Cart can contain medicines from only one pharmacy"
            );
        }

        CartItem item = cartItemRepository
                .findByCartIdAndInventoryId(
                        cart.getId(),
                        inventory.getId()
                )
                .orElseGet(CartItem::new);

        int newQuantity = item.getId() == null
                ? request.getQuantity()
                : item.getQuantity() + request.getQuantity();

        validateInventory(inventory, newQuantity);

        item.setCart(cart);
        item.setInventory(inventory);
        item.setQuantity(newQuantity);

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    public CartResponse getCart(String customerEmail) {

        User customer = getCustomer(customerEmail);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart is empty")
                );

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(
            String customerEmail,
            Long itemId,
            UpdateCartItemRequest request
    ) {

        User customer = getCustomer(customerEmail);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart not found")
                );

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found")
                );

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException(
                    "This cart item does not belong to the customer"
            );
        }

        validateInventory(
                item.getInventory(),
                request.getQuantity()
        );

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(
            String customerEmail,
            Long itemId
    ) {

        User customer = getCustomer(customerEmail);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart not found")
                );

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found")
                );

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException(
                    "This cart item does not belong to the customer"
            );
        }

        cartItemRepository.delete(item);

        List<CartItem> remainingItems =
                cartItemRepository.findByCartId(cart.getId());

        if (remainingItems.isEmpty()) {
            cartRepository.delete(cart);

            return new CartResponse(
                    null,
                    null,
                    null,
                    List.of(),
                    BigDecimal.ZERO
            );
        }

        return buildCartResponse(cart);
    }

    @Transactional
    public void clearCart(String customerEmail) {

        User customer = getCustomer(customerEmail);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart not found")
                );

        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);
    }

    private User getCustomer(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found")
                );
    }

    private void validateInventory(
            PharmacyInventory inventory,
            int requestedQuantity
    ) {

        if (!inventory.isActive()) {
            throw new IllegalArgumentException(
                    "Inventory is inactive"
            );
        }

        if (inventory.getPharmacy().getApprovalStatus()
                != PharmacyApprovalStatus.APPROVED) {

            throw new IllegalArgumentException(
                    "Pharmacy is not approved"
            );
        }

        if (!inventory.getExpiryDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Medicine is expired"
            );
        }

        if (requestedQuantity > inventory.getQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity is not available"
            );
        }
    }

    private CartResponse buildCartResponse(Cart cart) {

        List<CartItemResponse> items =
                cartItemRepository.findByCartId(cart.getId())
                        .stream()
                        .map(this::mapItem)
                        .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getPharmacy().getId(),
                cart.getPharmacy().getPharmacyName(),
                items,
                total
        );
    }

    private CartItemResponse mapItem(CartItem item) {

        BigDecimal subtotal =
                item.getInventory().getSellingPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        );

        return new CartItemResponse(
                item.getId(),
                item.getInventory().getId(),
                item.getInventory().getMedicine().getId(),
                item.getInventory().getMedicine().getMedicineName(),
                item.getInventory().getMedicine().getStrength(),
                item.getQuantity(),
                item.getInventory().getSellingPrice(),
                subtotal
        );
    }
}