package com.medifind.backend.controller;

import com.medifind.backend.entity.User;
import com.medifind.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<User>> getAllCustomers() {
        return ResponseEntity.ok(
                adminService.getAllCustomers()
        );
    }

    @GetMapping("/customers/count")
    public ResponseEntity<Map<String, Long>> getCustomerCount() {
        return ResponseEntity.ok(
                Map.of(
                        "totalCustomers",
                        adminService.getCustomerCount()
                )
        );
    }
}