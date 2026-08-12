package com.medifind.backend.controller;

import com.medifind.backend.dto.request.AddCartItemRequest;
import com.medifind.backend.dto.request.UpdateCartItemRequest;
import com.medifind.backend.dto.response.CartResponse;
import com.medifind.backend.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cartService.addItem(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cartService.getCart(authentication.getName())
        );
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateItem(
                        authentication.getName(),
                        itemId,
                        request
                )
        );
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(
                cartService.removeItem(
                        authentication.getName(),
                        itemId
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            Authentication authentication
    ) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }
}