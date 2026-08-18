package com.medifind.backend.controller;

import com.medifind.backend.dto.request.RegisterRequest;
import com.medifind.backend.dto.response.RegisterResponse;
import com.medifind.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.medifind.backend.dto.request.LoginRequest;
import com.medifind.backend.dto.response.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerCustomer(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response =
                authService.registerCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<RegisterResponse> registerAdmin(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response =
                authService.registerAdmin(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}